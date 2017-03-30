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
    section("Monitor this lock"){
        input "thelock", "capability.lock", required: true
    }
    section("Also lock these doors together...") {
        input "locks", "capability.lock", multiple: true, required: false
    }
    section("Also unlock these doors together...") {
        input "unlocks", "capability.lock", multiple: true, required: false
    }
    // TODO: Optional time and day restrictions
}


def initialize() {
    if (locks) {
      subscribe(thelock, "lock", lockChangedHandler)
    }
    if (unlocks) {
      subscribe(thelock, "unlock", lockChangedHandler)
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
    if (evt.value == "locked") {
        locks?.lock()
    } else if (evt.value == "unlocked") {
        unlocks?.unlock()
    }
}
