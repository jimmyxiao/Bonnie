//
//  RestClient.swift
//  iOSTemplate
//
//  Created by Professor on 1/25/16.
//  Copyright © 2016 Professor. All rights reserved.
//

import Foundation

class RestClient: NSObject, URLSessionTaskDelegate {
    var components: URLComponents
    private var session: URLSession?
    private var task: URLSessionDataTask?
    private var completionHandler: ((_ success: Bool, _ data: [String: Any]?) -> Void)?

    init(scheme: String, host: String, path: String? = nil) {
        components = URLComponents()
        components.scheme = scheme
        components.host = host
        if let path = path {
            components.path = path
        }
        super.init()
        session = URLSession(configuration: .default, delegate: self, delegateQueue: .main)
    }

    func getResponse(queries: [URLQueryItem]? = nil, data: [String: Any]? = nil, completionHandler: @escaping (_ success: Bool, _ data: [String: Any]?) -> Void) {
        self.completionHandler = completionHandler
        components.queryItems = queries
        guard let url = components.url else {
            Logger.p("\(#function) Error unwrapping url")
            failResponse()
            return
        }
        var request = URLRequest(url: url)
        request.addValue("application/json", forHTTPHeaderField: "Accept")
        if let data = data {
            do {
                request.httpBody = try JSONSerialization.data(withJSONObject: data, options: .prettyPrinted)
                request.httpMethod = "POST"
                request.addValue("application/json", forHTTPHeaderField: "Content-Type")
            } catch let error {
                Logger.p("\(#function): \(error.localizedDescription)")
                failResponse()
            }
        }
        getResponse(request: request)
    }

    func cancel() {
        task?.cancel()
    }

    private func getResponse(request: URLRequest) {
        cancel()
        self.task = session?.dataTask(with: request) {
            data, response, error in
            if let error = error as NSError? {
                Logger.p("\(#function) Error while making a call to \(request.url?.absoluteString ?? "") \(error.localizedDescription)")
                if error.code != NSURLErrorCancelled {
                    self.failResponse()
                }
            } else {
                guard let response = response as? HTTPURLResponse else {
                    Logger.p("\(#function) Error unwrapping response from \(request.url?.absoluteString ?? "")")
                    self.failResponse()
                    return
                }
                guard response.statusCode / 100 == 2 else {
                    Logger.p("\(#function) Error getting response from \(request.url?.absoluteString ?? "") with Status: \(response.statusCode)")
                    self.failResponse()
                    return
                }
                guard response.mimeType == "application/json" else {
                    Logger.p("\(#function) Error getting response from \(request.url?.absoluteString ?? "") with Content-Type: \(String(describing: response.mimeType))")
                    self.failResponse()
                    return
                }
                guard let data = data else {
                    Logger.p("\(#function) Error unwrapping data from \(request.url?.absoluteString ?? "")")
                    self.failResponse()
                    return
                }
                do {
                    let jsonObject = try JSONSerialization.jsonObject(with: data, options: .allowFragments) as? [String: Any]
                    DispatchQueue.main.async {
                        self.completionHandler?(true, jsonObject)
                    }
                } catch {
                    Logger.p("\(#function) Error deserializing data from \(request.url?.absoluteString ?? "")")
                    self.failResponse()
                }
            }
        }
        DispatchQueue.global().async {
            self.task?.resume()
        }
    }

    func urlSession(_ session: URLSession, didReceive challenge: URLAuthenticationChallenge, completionHandler: @escaping (URLSession.AuthChallengeDisposition, URLCredential?) -> Void) {
        switch challenge.protectionSpace.authenticationMethod {
        case NSURLAuthenticationMethodServerTrust:
            if let trust = challenge.protectionSpace.serverTrust {
                completionHandler(.useCredential, URLCredential(trust: trust))
            }
        default:
            challenge.sender?.performDefaultHandling?(for: challenge)
        }
    }

    private func failResponse() {
        DispatchQueue.main.async {
            self.completionHandler?(false, nil)
        }
    }
}
