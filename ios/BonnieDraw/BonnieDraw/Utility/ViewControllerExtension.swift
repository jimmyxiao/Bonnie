//
//  ViewControllerExtension.swift
//  Omna
//
//  Created by Jason Hsu 08329 on 10/24/16.
//  Copyright © 2016 D-Link. All rights reserved.
//

import HomeKit
import Photos
import UserNotifications

extension UIViewController {
    func presentAlert(title: String? = nil, message: String? = nil, buttonText: String? = nil, handler: ((UIAlertAction) -> Void)? = nil) {
        let alert = UIAlertController(title: title, message: message, preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: buttonText ?? "alert_button_confirm".localized, style: .cancel, handler: handler))
        alert.view.tintColor = UIColor.getAccentColor()
        present(alert, animated: true)
    }

    func presentConfirmationAlert(title: String? = nil, message: String? = nil, positiveTitle: String? = nil, negativeTitle: String? = nil, handler: @escaping ((Bool) -> Void)) {
        let alert = UIAlertController(title: title, message: message, preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: positiveTitle ?? "alert_button_confirm".localized, style: .default) {
            action in
            handler(true)
        })
        alert.addAction(UIAlertAction(title: negativeTitle ?? "alert_button_cancel".localized, style: .cancel) {
            action in
            handler(false)
        })
        alert.view.tintColor = UIColor.getAccentColor()
        present(alert, animated: true)
    }

    func checkPhotosPermission(successHandler: (() -> Void)? = nil, failHandler: (() -> Void)? = nil) {
        if PHPhotoLibrary.authorizationStatus() == .authorized {
            successHandler?()
        } else {
            let showAlert = PHPhotoLibrary.authorizationStatus() != .notDetermined
            PHPhotoLibrary.requestAuthorization() {
                status in
                DispatchQueue.main.async {
                    if status == .authorized {
                        successHandler?()
                    } else {
                        if showAlert {
                            self.presentConfirmationAlert(title: "alert_permission_required_title".localized, message: "alert_permission_photos".localized) {
                                success in
                                if success {
                                    UIApplication.shared.openSettings()
                                }
                                failHandler?()
                            }
                        } else {
                            failHandler?()
                        }
                    }
                }
            }
        }
    }

    func checkCameraPermission(successHandler: (() -> Void)? = nil, failHandler: (() -> Void)? = nil) {
        if AVCaptureDevice.authorizationStatus(for: AVMediaType.video) == .authorized {
            successHandler?()
        } else {
            let showAlert = AVCaptureDevice.authorizationStatus(for: AVMediaType.video) != .notDetermined
            AVCaptureDevice.requestAccess(for: AVMediaType.video) {
                status in
                DispatchQueue.main.async {
                    if status {
                        successHandler?()
                    } else {
                        if showAlert {
                            self.presentConfirmationAlert(title: "alert_permission_required_title".localized, message: "alert_permission_camera".localized) {
                                success in
                                if success {
                                    UIApplication.shared.openSettings()
                                }
                                failHandler?()
                            }
                        } else {
                            failHandler?()
                        }
                    }
                }
            }
        }
    }

    func checkMicrophonePermission(successHandler: (() -> Void)? = nil, failHandler: (() -> Void)? = nil) {
        if AVAudioSession.sharedInstance().recordPermission == .granted {
            successHandler?()
        } else {
            let showAlert = AVAudioSession.sharedInstance().recordPermission != .undetermined
            AVAudioSession.sharedInstance().requestRecordPermission() {
                status in
                DispatchQueue.main.async {
                    if status {
                        successHandler?()
                    } else {
                        if showAlert {
                            self.presentConfirmationAlert(title: "alert_permission_required_title".localized, message: "alert_permission_camera".localized) {
                                success in
                                if success {
                                    UIApplication.shared.openSettings()
                                }
                                failHandler?()
                            }
                        } else {
                            failHandler?()
                        }
                    }
                }
            }
        }
    }

    func checkNotificationPermission(successHandler: (() -> Void)? = nil, failHandler: (() -> Void)? = nil) {
        UNUserNotificationCenter.current().getNotificationSettings() {
            settings in
            if settings.authorizationStatus == .authorized {
                DispatchQueue.main.async {
                    successHandler?()
                }
            } else {
                let showAlert = settings.authorizationStatus != .notDetermined
                UNUserNotificationCenter.current().requestAuthorization(options: [.alert]) {
                    success, error in
                    DispatchQueue.main.async {
                        if success {
                            successHandler?()
                        } else {
                            if showAlert {
                                self.presentConfirmationAlert(title: "alert_permission_required_title".localized, message: "alert_permission_notification".localized) {
                                    success in
                                    if success {
                                        UIApplication.shared.openSettings()
                                    }
                                    failHandler?()
                                }
                            } else {
                                failHandler?()
                            }
                        }
                    }
                }
            }
        }
    }

    func getDirection(with location: (latitude: String, longitude: String), mode: String) {
        if let url = URL(string: "comgooglemaps://?daddr=\(location.latitude),\(location.longitude)&directionsmode=\(mode)"), UIApplication.shared.canOpenURL(url) {
            UIApplication.shared.open(url, options: [:])
        } else if let url = URL(string: "https://www.google.com/maps/dir/?api=1&destination=\(location.latitude),\(location.longitude)&travelmode=\(mode)") {
            UIApplication.shared.open(url, options: [:])
        }
    }

    func getDirection(with query: String) {
        let query = query.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed) ?? ""
        if let url = URL(string: "comgooglemaps://?q=\(query)"), UIApplication.shared.canOpenURL(url) {
            UIApplication.shared.open(url, options: [:])
        } else if let url = URL(string: "https://www.google.com/maps/search/?api=1&query=\(query)") {
            UIApplication.shared.open(url, options: [:])
        }
    }
}
