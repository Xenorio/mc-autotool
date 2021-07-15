package chneau.autotool

import net.fabricmc.api.ClientModInitializer

class Main : ClientModInitializer {
    override fun onInitializeClient() {
        Autotool().register()
        Autofarm().register()
        Autoattack().register()
    }
}
