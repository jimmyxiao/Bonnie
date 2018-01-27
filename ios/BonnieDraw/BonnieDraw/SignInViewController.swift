//
//  SignInViewController.swift
//  BonnieDraw
//
//  Created by Professor on 15/09/2017.
//  Copyright © 2017 Professor. All rights reserved.
//

import UIKit
import FacebookCore
import FacebookLogin
import TwitterKit
import Alamofire
import DeviceKit

class SignInViewController: UIViewController, GIDSignInDelegate, GIDSignInUIDelegate, UITextFieldDelegate {
    @IBOutlet weak var loading: LoadingIndicatorView!
    @IBOutlet weak var email: UITextField!
    @IBOutlet weak var password: UITextField!
    private var dataRequest: DataRequest?
    private var completionHandler: ((String?) -> Void)?

    override func viewDidLoad() {
        if DEBUG {
            navigationItem.rightBarButtonItem = UIBarButtonItem(barButtonSystemItem: .cancel, target: self, action: #selector(launchMain))
        }
        if let path = Bundle.main.path(forResource: "GoogleService-Info", ofType: "plist") {
            let dictionary = NSDictionary(contentsOfFile: path)
            GIDSignIn.sharedInstance().uiDelegate = self
            GIDSignIn.sharedInstance().delegate = self
            GIDSignIn.sharedInstance().clientID = dictionary?.object(forKey: "CLIENT_ID") as? String
        }
    }

    override func viewWillDisappear(_ animated: Bool) {
        dataRequest?.cancel()
        NotificationCenter.default.removeObserver(self)
    }

    @IBAction func signIn(_ sender: Any) {
        guard AppDelegate.reachability.connection != .none else {
            presentDialog(title: "app_network_unreachable_title".localized, message: "app_network_unreachable_content".localized)
            return
        }
        let email = self.email.text?.trimmingCharacters(in: .whitespacesAndNewlines) ?? ""
        let password = self.password.text ?? ""
        if email.isEmpty {
            presentDialog(title: "alert_sign_in_fail_title".localized, message: "alert_sign_in_fail_email_empty".localized) {
                action in
                self.email.becomeFirstResponder()
            }
        } else if !email.isValidEmail() {
            presentDialog(title: "alert_sign_in_fail_title".localized, message: "alert_sign_in_fail_email_invaid".localized) {
                action in
                self.email.becomeFirstResponder()
            }
        } else if password.isEmpty {
            presentDialog(title: "alert_sign_in_fail_title".localized, message: "alert_sign_in_fail_password_empty".localized) {
                action in
                self.password.becomeFirstResponder()
            }
        } else if password.count < 4 {
            presentDialog(title: "alert_sign_in_fail_title".localized, message: "alert_sign_in_fail_password_invalid".localized) {
                action in
                self.password.becomeFirstResponder()
            }
        } else {
            view.endEditing(true)
            loading.hide(false)
            self.completionHandler = {
                token in
                let securePassword = password.MD5()
                var postData: [String: Any] = ["uc": email, "up": securePassword, "ut": 1, "dt": SERVICE_DEVICE_TYPE, "fn": 1]
                let locale = Locale.current
                if let languageCode = locale.languageCode {
                    postData["languageCode"] = languageCode
                }
                if let regionCode = locale.regionCode {
                    postData["countryCode"] = regionCode
                }
                if let deviceId = UIDevice.current.identifierForVendor?.uuidString {
                    postData["deviceId"] = deviceId
                }
                if let token = token {
                    postData["token"] = token
                }
                self.dataRequest = Alamofire.request(
                        Service.standard(withPath: Service.LOGIN),
                        method: .post,
                        parameters: postData,
                        encoding: JSONEncoding.default).validate().responseJSON {
                    response in
                    switch response.result {
                    case .success:
                        guard let data = response.result.value as? [String: Any], let response = data["res"] as? Int else {
                            self.showErrorMessage(message: "app_network_unreachable_content".localized)
                            return
                        }
                        if response == 1, let token = data["lk"] as? String, let userId = data["ui"] as? Int {
                            self.dataRequest = Alamofire.request(
                                    Service.standard(withPath: Service.USER_INFO_QUERY),
                                    method: .post,
                                    parameters: ["ui": userId, "lk": token, "dt": SERVICE_DEVICE_TYPE],
                                    encoding: JSONEncoding.default).validate().responseJSON {
                                response in
                                switch response.result {
                                case .success:
                                    guard let data = response.result.value as? [String: Any] else {
                                        self.showErrorMessage(message: "app_network_unreachable_content".localized)
                                        return
                                    }
                                    let profile = Profile(withDictionary: data)
                                    let defaults = UserDefaults.standard
                                    defaults.set(token, forKey: Default.TOKEN)
                                    defaults.set(userId, forKey: Default.USER_ID)
                                    defaults.set(email, forKey: Default.EMAIL)
                                    defaults.set(securePassword, forKey: Default.PASSWORD)
                                    defaults.set(UserType.email.rawValue, forKey: Default.USER_TYPE)
                                    defaults.set(Date(), forKey: Default.TOKEN_TIMESTAMP)
                                    defaults.set(profile.image, forKey: Default.IMAGE)
                                    defaults.set(profile.name, forKey: Default.NAME)
                                    self.launchMain()
                                case .failure(let error):
                                    if let error = error as? URLError, error.code == .cancelled {
                                        return
                                    }
                                    self.showErrorMessage(message: error.localizedDescription)
                                }
                            }
                        } else {
                            self.showErrorMessage(message: data["msg"] as? String)
                        }
                    case .failure(let error):
                        if let error = error as? URLError, error.code == .cancelled {
                            return
                        }
                        self.showErrorMessage(message: error.localizedDescription)
                    }
                }
            }
            self.registerForRemoteNotification()
        }
    }

    @IBAction func facebook(_ sender: Any) {
        loading.hide(false)
        LoginManager().logIn(readPermissions: [.publicProfile, .userFriends, .email], viewController: self) {
            result in
            switch result {
            case .success(_, _, let accessToken):
                GraphRequest(graphPath: "me", parameters: ["fields": "name,email"]).start() {
                    response, result in
                    switch result {
                    case .success(let response):
                        guard let facebookId = response.dictionaryValue?["id"] as? String,
                              let facebookName = response.dictionaryValue?["name"] as? String else {
                            self.presentDialog(title: "alert_sign_in_fail_title".localized, message: "app_network_unreachable_content".localized)
                            self.loading.hide(true)
                            return
                        }
                        if let facebookEmail = response.dictionaryValue?["email"] as? String {
                            GraphRequest(graphPath: "/\(facebookId)/picture", parameters: ["redirect": false, "width": 128, "height": 128]).start() {
                                response, result in
                                switch result {
                                case .success(let response):
                                    var imageUrl: URL? = nil
                                    if let data = response.dictionaryValue?["data"] as? [String: Any],
                                       let imageUrlString = data["url"] as? String {
                                        imageUrl = URL(string: imageUrlString)
                                    }
                                    self.completionHandler = {
                                        token in
                                        self.signInThirdParty(withRemoteToken: token, thirdPartyAccessToken: accessToken.authenticationToken, type: .facebook, userId: facebookId, name: facebookName, email: facebookEmail, imageUrl: imageUrl)
                                    }
                                    self.registerForRemoteNotification()
                                case .failed(let error):
                                    self.presentDialog(title: "alert_sign_in_fail_title".localized, message: error.localizedDescription)
                                    fallthrough
                                default:
                                    self.loading.hide(true)
                                }
                            }
                        } else {
                            self.presentDialog(title: "alert_sign_in_fail_title".localized, message: "alert_sign_in_fail_email_permission".localized)
                            self.loading.hide(true)
                        }
                    case .failed(let error):
                        self.presentDialog(title: "alert_sign_in_fail_title".localized, message: error.localizedDescription)
                        fallthrough
                    default:
                        self.loading.hide(true)
                    }
                }
            case .failed(let error):
                self.presentDialog(title: "alert_sign_in_fail_title".localized, message: error.localizedDescription)
                fallthrough
            default:
                self.loading.hide(true)
            }
        }
    }

    @IBAction func twitter(_ sender: Any) {
        loading.hide(false)
        TWTRTwitter.sharedInstance().logIn() {
            session, error in
            if let error = error as NSError? {
                if error.code != TWTRLogInErrorCode.logInErrorCodeCancelled.rawValue {
                    self.presentDialog(title: "alert_sign_in_fail_title".localized, message: error.localizedDescription)
                }
                self.loading.hide(true)
            } else {
                TWTRAPIClient.withCurrentUser().requestEmail() {
                    email, error in
                    if let error = error {
                        self.presentDialog(title: "alert_sign_in_fail_title".localized, message: error.localizedDescription)
                        self.loading.hide(true)
                    } else {
                        TWTRAPIClient.withCurrentUser().loadUser(withID: session!.userID) {
                            user, error in
                            self.completionHandler = {
                                token in
                                self.signInThirdParty(withRemoteToken: token, thirdPartyAccessToken: session!.authToken, type: .twitter, userId: session!.userID, name: user?.name ?? session!.userName, email: email!, imageUrl: URL(string: user?.profileImageLargeURL ?? ""))
                            }
                            self.registerForRemoteNotification()
                        }
                    }
                }
            }
        }
    }

    @IBAction func google(_ sender: Any) {
        loading.hide(false)
        GIDSignIn.sharedInstance().scopes = ["https://www.googleapis.com/auth/contacts.readonly"]
        GIDSignIn.sharedInstance().signIn()
    }

    internal func sign(_ signIn: GIDSignIn!, didSignInFor user: GIDGoogleUser!, withError error: Error!) {
        if let error = error as NSError? {
            if error.code != GIDSignInErrorCode.canceled.rawValue {
                presentDialog(title: "alert_sign_in_fail_title".localized, message: error.localizedDescription)
            }
            loading.hide(true)
        } else {
            self.completionHandler = {
                token in
                self.signInThirdParty(withRemoteToken: token, thirdPartyAccessToken: user.authentication.accessToken, type: .google, userId: user.userID, name: user.profile.name, email: user.profile.email, imageUrl: user.profile.imageURL(withDimension: 128))
            }
            self.registerForRemoteNotification()
        }
    }

    private func showErrorMessage(message: String?) {
        UIApplication.shared.unregisterForRemoteNotifications()
        presentDialog(title: "alert_sign_in_fail_title".localized, message: message)
        loading.hide(true)
        password.text = nil
    }

    @objc private func launchMain() {
        UIApplication.shared.replace(rootViewControllerWith: UIStoryboard(name: Device().isPad ? "Main_iPad" : "Main", bundle: nil).instantiateViewController(withIdentifier: Identifier.PARENT))
    }

    private func registerForRemoteNotification() {
        checkNotificationPermission(
                successHandler: {
                    if !Device().isSimulator {
                        NotificationCenter.default.addObserver(self, selector: #selector(self.didRegisterForRemoteNotifications), name: Notification.Name(rawValue: NotificationName.REMOTE_TOKEN), object: nil)
                        UIApplication.shared.registerForRemoteNotifications()
                    } else {
                        self.completionHandler?(nil)
                    }
                },
                failHandler: {
                    self.completionHandler?(nil)
                })
    }

    @objc private func didRegisterForRemoteNotifications(notification: Notification) {
        NotificationCenter.default.removeObserver(self)
        completionHandler?(notification.object as? String)
    }

    private func signInThirdParty(withRemoteToken remoteToken: String?, thirdPartyAccessToken accessToken: String, type: UserType, userId id: String, name: String, email: String, imageUrl: URL?) {
        var postData: [String: Any] = ["uc": id, "un": name, "ut": type.rawValue, "dt": SERVICE_DEVICE_TYPE, "fn": 1, "thirdEmail": email]
        let locale = Locale.current
        if let languageCode = locale.languageCode {
            postData["languageCode"] = languageCode
        }
        if let regionCode = locale.regionCode {
            postData["countryCode"] = regionCode
        }
        if let deviceId = UIDevice.current.identifierForVendor?.uuidString {
            postData["deviceId"] = deviceId
        }
        if let token = remoteToken {
            postData["token"] = token
        }
        if let imageUrl = imageUrl {
            postData["thirdPictureUrl"] = imageUrl.absoluteString
        }
        dataRequest = Alamofire.request(
                Service.standard(withPath: Service.LOGIN),
                method: .post,
                parameters: postData,
                encoding: JSONEncoding.default).validate().responseJSON {
            response in
            switch response.result {
            case .success:
                guard let data = response.result.value as? [String: Any], let response = data["res"] as? Int else {
                    self.showErrorMessage(message: "app_network_unreachable_content".localized)
                    return
                }
                if response == 1, let token = data["lk"] as? String, let userId = data["ui"] as? Int {
                    self.dataRequest = Alamofire.request(
                            Service.standard(withPath: Service.USER_INFO_QUERY),
                            method: .post,
                            parameters: ["ui": userId, "lk": token, "dt": SERVICE_DEVICE_TYPE],
                            encoding: JSONEncoding.default).validate().responseJSON {
                        response in
                        switch response.result {
                        case .success:
                            guard let data = response.result.value as? [String: Any] else {
                                self.showErrorMessage(message: "app_network_unreachable_content".localized)
                                return
                            }
                            let profile = Profile(withDictionary: data)
                            let defaults = UserDefaults.standard
                            defaults.set(token, forKey: Default.TOKEN)
                            defaults.set(userId, forKey: Default.USER_ID)
                            defaults.set(type.rawValue, forKey: Default.USER_TYPE)
                            defaults.set(Date(), forKey: Default.TOKEN_TIMESTAMP)
                            defaults.set(accessToken, forKey: Default.THIRD_PARTY_TOKEN)
                            defaults.set(id, forKey: Default.THIRD_PARTY_ID)
                            defaults.set(email, forKey: Default.THIRD_PARTY_EMAIL)
                            defaults.set(name, forKey: Default.NAME)
                            if let imageUrl = profile.image {
                                defaults.set(imageUrl, forKey: Default.IMAGE)
                            } else {
                                defaults.set(imageUrl, forKey: Default.IMAGE)
                            }
                            self.launchMain()
                        case .failure(let error):
                            if let error = error as? URLError, error.code == .cancelled {
                                return
                            }
                            self.showErrorMessage(message: error.localizedDescription)
                        }
                    }
                } else {
                    self.showErrorMessage(message: data["msg"] as? String)
                }
            case .failure(let error):
                if let error = error as? URLError, error.code == .cancelled {
                    return
                }
                self.showErrorMessage(message: error.localizedDescription)
            }
        }
    }

    internal func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        if textField == email {
            password.becomeFirstResponder()
            return false
        }
        textField.resignFirstResponder()
        return true
    }
}
