package org.rev317.min;

import org.parabot.core.Context;
import org.parabot.core.Core;
import org.parabot.core.Directories;
import org.parabot.core.asm.ASMClassLoader;
import org.parabot.core.asm.adapters.AddInterfaceAdapter;
import org.parabot.core.asm.hooks.HookFile;
import org.parabot.core.desc.ServerProviderInfo;
import org.parabot.core.reflect.RefClass;
import org.parabot.core.ui.components.VerboseLoader;
import org.parabot.environment.api.utils.WebUtil;
import org.parabot.environment.scripts.Script;
import org.parabot.environment.servers.ServerManifest;
import org.parabot.environment.servers.ServerProvider;
import org.parabot.environment.servers.Type;
import org.rev317.min.accessors.Client;
import org.rev317.min.script.ScriptEngine;
import org.rev317.min.ui.BotMenu;

import javax.swing.*;
import java.applet.Applet;
import java.io.File;
import java.net.InetAddress;
import java.net.URL;

/**
 * @author Everel, JKetelaar
 */
@ServerManifest(author = "Everel & JKetelaar", name = "Server name here", type = Type.INJECTION, version = 2.1)
public class Loader extends ServerProvider {
    private boolean extended = true;

    public static Client getClient() {
        return (Client) Context.getInstance().getClient();
    }

    @Override
    public Applet fetchApplet() {
        try {
            final Context        context     = Context.getInstance();
            final ASMClassLoader classLoader = context.getASMClassLoader();

            System.setProperty("java.net.preferIPv4Stack", "true");
            System.setProperty("java.net.preferIPv6Addresses", "false");
            System.setProperty("sun.java2d.uiScale", "1");

            Class<?> bmClass = classLoader.loadClass("pkhonor.bM");
            bmClass.getDeclaredMethod("PsDw", new Class[] {int.class, String.class, int.class, boolean.class})
                    .invoke(null, -1, System.getProperty("user.home"), 2, false);

            final RefClass clientClass = new RefClass(classLoader.loadClass("pkhonor.Client"));
            clientClass.getField("QaOL", "Z").setBoolean(false);
            clientClass.getField("LdRS", "I").setInt(1);
            clientClass.getField("JmV", "I").setInt(0);
            clientClass.getMethod("Zuq").invoke();
            clientClass.getField("RKA", "Z").setBoolean(true);
            final RefClass Ce = new RefClass(classLoader.loadClass("pkhonor.Ce"));
            Ce.getField("oW", "I").setInt(32);

            final RefClass client = clientClass.newInstance();
            clientClass.getField("PsDw", "Lpkhonor/Client;").set(client.getInstance());

            Ce.getMethod("PsDw", new Class[] { InetAddress.class}).invoke(InetAddress.getLocalHost());

            return (Applet) client.getInstance();
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

    @Override
    public URL getJar() {
        ServerProviderInfo serverProvider = Context.getInstance().getServerProviderInfo();

        File target = new File(Directories.getCachePath(), serverProvider.getClientCRC32() + ".jar");
        if (!target.exists()) {
            WebUtil.downloadFile(serverProvider.getClient(), target, VerboseLoader.get());
        }

        return WebUtil.toURL(target);
    }

    @Override
    public void addMenuItems(JMenuBar bar) {
        new BotMenu(bar);
    }

    @Override
    public void injectHooks() {
        AddInterfaceAdapter.setAccessorPackage("org/rev317/min/accessors/");
        try {
            super.injectHooks();
        } catch (Exception e) {
            if (Core.inVerboseMode()) {
                e.printStackTrace();
            }
            this.extended = false;
            super.injectHooks();
        }
    }

    @Override
    public void initScript(Script script) {
        ScriptEngine.getInstance().setScript(script);
        ScriptEngine.getInstance().init();
    }

    @Override
    public HookFile getHookFile() {
        if (this.extended) {
            return new HookFile(Context.getInstance().getServerProviderInfo().getExtendedHookFile(), HookFile.TYPE_XML);
        } else {
            return new HookFile(Context.getInstance().getServerProviderInfo().getHookFile(), HookFile.TYPE_XML);
        }
    }

    public void unloadScript(Script script) {
        ScriptEngine.getInstance().unload();
    }

    @Override
    public void init() {
    }
}
