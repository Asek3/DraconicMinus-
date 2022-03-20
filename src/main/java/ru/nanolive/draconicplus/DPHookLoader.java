package ru.nanolive.draconicplus;

import alexsocol.hooks.ASJASM;
import gloomyfolken.hooklib.minecraft.HookLoader;
import gloomyfolken.hooklib.minecraft.PrimaryClassTransformer;

public class DPHookLoader extends HookLoader {

	@Override
	public String[] getASMTransformerClass() {
	    return new String[]{PrimaryClassTransformer.class.getName(), ASJASM.class.getName()};
	}
	
	@Override
	public void registerHooks() {
        ASJASM.registerFieldHookContainer("ru.nanolive.draconicplus.DPTessellatorHooks");
		
		registerHookContainer("ru.nanolive.draconicplus.DPTessellatorHooks");
	}

}
