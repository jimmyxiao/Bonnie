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
import Google
import Alamofire

class SignInViewController: UIViewController, GIDSignInDelegate, GIDSignInUIDelegate, UITextFieldDelegate {
    @IBOutlet weak var loading: LoadingIndicatorView!
    @IBOutlet weak var email: UITextField!
    @IBOutlet weak var password: UITextField!
    @IBOutlet weak var google: UIButton!
    private var dataRequest: DataRequest?

    override func viewDidLoad() {
        if DEBUG {
            navigationItem.rightBarButtonItem = UIBarButtonItem(barButtonSystemItem: .cancel, target: self, action: #selector(launchMain))
        }
        GIDSignIn.sharedInstance().uiDelegate = self
        GIDSignIn.sharedInstance().delegate = self
        var configureError: NSError?
        GGLContext.sharedInstance().configureWithError(&configureError)
        if let error = configureError {
            Logger.d("\(#function): \(error.localizedDescription)")
            google.isEnabled = false
        }
    }

    override func viewWillDisappear(_ animated: Bool) {
        dataRequest?.cancel()
    }

    @IBAction func signIn(_ sender: Any) {
        guard AppDelegate.reachability.connection != .none else {
            presentDialog(title: "app_network_unreachable_title".localized, message: "app_network_unreachable_content".localized)
            return
        }
        let email = self.email.text?.trimmingCharacters(in: .whitespacesAndNewlines) ?? ""
        var password = self.password.text ?? ""
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
        } else if password.characters.count < 4 {
            presentDialog(title: "alert_sign_in_fail_title".localized, message: "alert_sign_in_fail_password_invalid".localized) {
                action in
                self.password.becomeFirstResponder()
            }
        } else {
            let securePassword = password.MD5()
            view.endEditing(true)
            loading.hide(false)
            dataRequest = Alamofire.request(
                    Service.standard(withPath: Service.LOGIN),
                    method: .post,
                    parameters: ["uc": email, "up": securePassword, "ut": 1, "dt": SERVICE_DEVICE_TYPE, "fn": 1],
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
                                Logger.d(token)
                                let defaults = UserDefaults.standard
                                defaults.set(token, forKey: Default.TOKEN)
                                defaults.set(userId, forKey: Default.USER_ID)
                                defaults.set(email, forKey: Default.EMAIL)
                                defaults.set(securePassword, forKey: Default.PASSWORD)
                                if let urlString = data["profilePicture"] as? String {
                                    defaults.set(URL(string: urlString), forKey: Default.THIRD_PARTY_IMAGE)
                                }
                                defaults.set(Date(), forKey: Default.TOKEN_TIMESTAMP)
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
    }

    @IBAction func facebook(_ sender: Any) {
        loading.hide(false)
        LoginManager().logIn(readPermissions: [.publicProfile, .email], viewController: self) {
            result in
            switch result {
            case .success(_, _, _):
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
                                    self.signInThirdParty(withUserType: .facebook, userId: facebookId, name: facebookName, email: facebookEmail, imageUrl: imageUrl)
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
        Twitter.sharedInstance().logIn() {
            session, error in
            if let error = error {
                self.presentDialog(title: "alert_sign_in_fail_title".localized, message: error.localizedDescription)
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
                            self.signInThirdParty(withUserType: .twitter, userId: session!.userID, name: user?.name ?? session!.userName, email: email!, imageUrl: URL(string: user?.profileImageLargeURL ?? ""))
                        }
                    }
                }
            }
        }
    }

    @IBAction func google(_ sender: Any) {
        loading.hide(false)
        GIDSignIn.sharedInstance().signIn()
    }

    internal func sign(_ signIn: GIDSignIn!, didSignInFor user: GIDGoogleUser!, withError error: Error!) {
        if let error = error {
            loading.hide(true)
            presentDialog(title: "alert_sign_in_fail_title".localized, message: error.localizedDescription)
        } else {
            signInThirdParty(withUserType: .google, userId: user.userID, name: user.profile.name, email: user.profile.email, imageUrl: user.profile.imageURL(withDimension: 128))
        }
    }

    private func showErrorMessage(message: String?) {
        presentDialog(title: "alert_sign_in_fail_title".localized, message: message)
        loading.hide(true)
        password.text = nil
    }

    @objc private func launchMain() {
        UIApplication.shared.replace(rootViewControllerWith: UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: Identifier.PARENT))
    }

    private func signInThirdParty(withUserType type: UserType, userId id: String, name: String, email: String, imageUrl: URL?) {
        var postData: [String: Any] = ["uc": id, "un": name, "ut": type.rawValue, "dt": SERVICE_DEVICE_TYPE, "fn": 1, "thirdEmail": email]
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
                            Logger.d(token)
                            let defaults = UserDefaults.standard
                            defaults.set(token, forKey: Default.TOKEN)
                            defaults.set(userId, forKey: Default.USER_ID)
                            defaults.set(type.rawValue, forKey: Default.USER_TYPE)
                            defaults.set(id, forKey: Default.THIRD_PARTY_ID)
                            defaults.set(name, forKey: Default.THIRD_PARTY_NAME)
                            defaults.set(email, forKey: Default.THIRD_PARTY_EMAIL)
                            if let urlString = data["profilePicture"] as? String {
                                defaults.set(URL(string: urlString), forKey: Default.THIRD_PARTY_IMAGE)
                            } else {
                                defaults.set(imageUrl, forKey: Default.THIRD_PARTY_IMAGE)
                            }
                            defaults.set(Date(), forKey: Default.TOKEN_TIMESTAMP)
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
