/**
 * Synchronize Locks
 *
 * Author: Spiros Papadimitriou
 *
 * This file is released under the MIT License:
 * https://opensource.org/licenses/MIT
 *
 * This software is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied.
 */

definition(
    name: "Synchronize Locks",
    namespace: "spapadim",
    author: "Spiros Papadimitriou",
    description: "Whenever the state of a lock changes, lock or unlock other doors as well",
    category: "Safety & Security",
    iconUrl: "https://raw.githubusercontent.com/spapadim/SmartThingsPublic/master/smartapps/spapadim/lock-sync.src/lock-sync.png",
    iconX2Url: "https://raw.githubusercontent.com/spapadim/SmartThingsPublic/master/smartapps/spapadim/lock-sync.src/lock-sync@2x.png"
)

preferences {
    section("Locks to keep synchronized"){
        input "master", "capability.lock", title: "Master lock (trigger)"
        input "slaves", "capability.lock", title: "Slave locks", multiple: true
    }
    section("Actions to synchronize") {
        input "syncLock", "bool", title: "Lock slaves when master locks", defaultValue: true
        input "syncUnlock", "bool", title: "Unlock slaves when master locks", defaultValue: false
    }
    section("Only if one of these people is present...") {
        input "people", "capability.presenceSensor", multiple: true, required: false
        input "alwaysLock", "bool", title: "Always lock", defaultValue: true
    }
}


def initialize() {
    if (syncLock) {
        subscribe(master, "lock", lockChangedHandler)
    }
    if (syncUnlock) {
        subscribe(master, "unlock", lockChangedHandler)
    }
}

def installed() {
  initialize()
}

def updated() {
    unsubscribe()
    initialize()
}

def lockChangedHandler(evt) {
    log.debug "$evt.value: $evt, $settings"
    def anyoneThere = (people == null || people.any{ it.currentPresence == "present" })
    log.debug "anyoneThere: $anyoneThere"
    if (syncLock && (anyoneThere || alwaysLock) && evt.value == "locked") {
        slaves.lock()
    } else if (syncUnlock && anyoneThere && evt.value == "unlocked") {
        slaves.unlock()
    }
}
