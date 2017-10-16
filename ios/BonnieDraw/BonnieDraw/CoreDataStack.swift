//
//  CoreDataStack.swift
//  iOSTemplate
//
//  Created by Professor on 7/11/16.
//  Copyright © 2016 Professor. All rights reserved.
//

import CoreData

struct CoreDataStack {
    private let coordinator: NSPersistentStoreCoordinator
    private let databaseURL: URL
    private let persistingContext: NSManagedObjectContext
    private let backgroundContext: NSManagedObjectContext
    let context: NSManagedObjectContext

    init?(modelName: String) {
        let modelUrl = Bundle.main.url(forResource: modelName, withExtension: "momd")!
        let model = NSManagedObjectModel(contentsOf: modelUrl)!
        coordinator = NSPersistentStoreCoordinator(managedObjectModel: model)
        persistingContext = NSManagedObjectContext(concurrencyType: .privateQueueConcurrencyType)
        persistingContext.persistentStoreCoordinator = coordinator
        context = NSManagedObjectContext(concurrencyType: .mainQueueConcurrencyType)
        context.parent = persistingContext
        backgroundContext = NSManagedObjectContext(concurrencyType: .privateQueueConcurrencyType)
        backgroundContext.parent = context
        let documentsUrl = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask).first!
        databaseURL = documentsUrl.appendingPathComponent("data.db")
        do {
            try coordinator.addPersistentStore(ofType: NSSQLiteStoreType, configurationName: nil, at: databaseURL, options: nil)
        } catch {
            Logger.d("\(#function): \(error.localizedDescription)")
        }
    }

    func dropAllData() {
        do {
            try coordinator.destroyPersistentStore(at: databaseURL, ofType: NSSQLiteStoreType, options: nil)
            try coordinator.addPersistentStore(ofType: NSSQLiteStoreType, configurationName: nil, at: databaseURL, options: nil)
        } catch {
            Logger.d("\(#function): \(error.localizedDescription)")
        }
    }

    func insertData(_ closure: @escaping (_ backgroundContext: NSManagedObjectContext) -> Void) {
        backgroundContext.perform() {
            closure(self.backgroundContext)
            do {
                try self.backgroundContext.save()
            } catch {
                Logger.d("\(#function): \(error.localizedDescription)")
            }
            self.saveContext()
        }
    }

    private func saveContext() {
        context.performAndWait() {
            if self.context.hasChanges {
                do {
                    try self.context.save()
                } catch {
                    fatalError("\(#function): \(error.localizedDescription)")
                }
                self.persistingContext.perform() {
                    do {
                        try self.persistingContext.save()
                    } catch {
                        fatalError("\(#function): \(error.localizedDescription)")
                    }
                }
            }
        }
    }

    func autoSave(_ delayInSeconds: Int) {
        if delayInSeconds > 0 {
            do {
                try saveContext()
                Logger.d("Autosaving")
            } catch {
                Logger.d("\(#function): \(error)")
            }
            let delayInNanoSeconds = UInt64(delayInSeconds) * NSEC_PER_SEC
            let time = DispatchTime.now() + Double(Int64(delayInNanoSeconds)) / Double(NSEC_PER_SEC)
            DispatchQueue.main.asyncAfter(deadline: time, execute: {
                self.autoSave(delayInSeconds)
            })
        }
    }
}
