package chneau.autotool

import net.fabricmc.api.ClientModInitializer

class Main : ClientModInitializer {
    override fun onInitializeClient() {
        (Autotool(select = SelectBest())).register()
        (Autofarm()).register()
        (Autoattack()).register()
    }
}
