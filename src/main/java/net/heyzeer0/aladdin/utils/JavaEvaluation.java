package net.heyzeer0.aladdin.utils;

import net.heyzeer0.aladdin.Main;
import net.heyzeer0.aladdin.configs.instances.BotConfig;
import net.heyzeer0.aladdin.profiles.commands.MessageEvent;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.rmi.UnexpectedException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by HeyZeer0 on 07/12/2016.
 * Copyright © HeyZeer0 - 2016
 */
public class JavaEvaluation {

    private static File folder = new File(Main.getDataFolder() + File.separator + "classes");
    private static File f; // Src
    private static File out;

    public static HashMap<String, Boolean> ks = new HashMap<>();

    private static StringBuilder imports = new StringBuilder();


    public static String eval(String objeto, MessageEvent evento) {

        folder.mkdirs();
        f = new File(folder + "/Evaluation.java");
        out = new File(folder + "/Evaluation.class");

        if(objeto.startsWith("-lmethods")) {
            objeto = "try { Class c = " + objeto.replace("-lmethods", "") + ".class; Method[] m = c.getDeclaredMethods(); for(int i = 0; i < m.length; i++) e.getChannel().sendMessage(m[i].toString()).queue();  }catch (Exception e) { e.printStackTrace(); }";
        }

        if(objeto.startsWith("-hashkey")) {
            objeto = "StringBuilder h = new StringBuilder(); h.append(\"```prolog\\n\"); for(Object obj : " + objeto.replace("-hashkey ", "") + ".keySet()) { h.append(Character.toUpperCase(obj.toString().charAt(0)) + obj.toString().substring(1) + \": \").append(" + objeto.replace("-hashkey ", "") + ".get(obj)).append(\"\\n\"); } h.append(\"```\"); e.sendMessage(h.toString());";
        }


        try {
            if (f.createNewFile()) f.deleteOnExit();

            OutputStream stream = new BufferedOutputStream(new FileOutputStream(f));
            stream.write(getBodyWithLines(objeto).getBytes());
            stream.close();

            String x = "";

            try {
                x = compile();
            } catch (Exception e) {
                e.printStackTrace();
                return x;
            }

            URLClassLoader urlClassLoader = new URLClassLoader(new URL[] {folder.toURL()}, Main.class.getClassLoader());

            try{
                Class clazz = urlClassLoader.loadClass(out.getName().replace(".class", ""));
                Object o = clazz.getConstructors()[0].newInstance(evento);

                try {
                    Object finalO = o;
                    FutureTask<?> task = new FutureTask<>(() -> finalO.getClass().getMethod("run").invoke(finalO));
                    task.run();
                    o = task.get(2, TimeUnit.SECONDS);
                } catch (TimeoutException e) {
                    return "``Tempo esgotado``";
                } catch (Exception e) {
                    return Utils.sendToHastebin(x);
                }
                if (o == null || o.toString().isEmpty())
                    o = "Executado sem erros e sem retorno!";
                o = o.toString().replace(BotConfig.bot_token, "<BOT TOKEN>");
                f.delete();
                out.delete();
                folder.delete();
                return "" + o.toString() + "";
            }catch (Exception e) {
                return Utils.sendToHastebin(x);
            }
        } catch (IOException e) {
            f.delete();
            out.delete();
            folder.delete();
            return e.getLocalizedMessage();
        }
    }

    private static String compile() throws Exception {
        if (!f.exists())
            throw new UnexpectedException("Unable to compile source file.");
        ProcessBuilder builder = new ProcessBuilder();
        builder.command("javac", "-cp", System.getProperty("java.class.path"), folder + "/" + f.getName());
        Process p = builder.start();

        Scanner sc = new Scanner(p.getInputStream());

        Scanner scErr = new Scanner(p.getErrorStream());

        StringWriter writer = new StringWriter();
        IOUtils.copy(p.getErrorStream(), writer, Charset.forName("UTF-8"));
        String x = writer.toString().replace(Main.getDataFolder().getPath(), "<ClassPath>").replace("\\classes", "");

        sc.close();
        scErr.close();

        p.waitFor();
        p.destroyForcibly();

        return x;
    }

    private static String getBodyWithLines(String code) {
        String body =
                        "import java.util.*;\n" +
                        "import java.math.*;\n" +
                        "import java.net.*;\n" +
                        "import java.io.*;\n" +
                        "import java.util.concurrent.*;\n" +
                        "import java.time.*;\n" +
                        "import java.lang.*;\n" +
                        "import java.lang.reflect.*;\n" +
                        "import java.util.stream.*;\n" +
                        "import net.dv8tion.jda.core.entities.*;\n" +
                        "import net.dv8tion.jda.core.entities.impl.*;\n" +
                        "import net.dv8tion.jda.core.*;\n" +
                        "import net.dv8tion.jda.core.managers.*;\n" +
                        "import net.dv8tion.jda.core.managers.fields.*;\n" +
                        "import net.dv8tion.jda.core.managers.impl.*;\n" +
                        "import net.heyzeer0.aladdin.*;\n" +
                        "import net.heyzeer0.aladdin.enums.*;\n" +
                        "import net.heyzeer0.aladdin.commands.*;\n" +
                        "import net.heyzeer0.aladdin.database.*;\n" +
                        "import net.heyzeer0.aladdin.database.entities.*;\n" +
                        "import net.heyzeer0.aladdin.database.interfaces.*;\n" +
                        "import net.heyzeer0.aladdin.manager.*;\n" +
                        "import net.heyzeer0.aladdin.manager.commands.*;\n" +
                        "import net.heyzeer0.aladdin.manager.utilities.*;\n" +
                        "import net.heyzeer0.aladdin.profiles.*;\n" +
                        "import net.heyzeer0.aladdin.profiles.commands.*;\n" +
                        "import net.heyzeer0.aladdin.profiles.custom.*;\n" +
                        "import net.heyzeer0.aladdin.profiles.permissions.*;\n" +
                        "import net.heyzeer0.aladdin.profiles.utilities.*;\n" +
                        "import net.heyzeer0.aladdin.utils.*;\n" +
                        "public class " + f.getName().replace(".java", "") + "\n{" +
                        "\n\tpublic Object run() throws Exception" +
                        "\n\t{\n\t\t";

        String[] lines = code.split("\n");
        body += String.join("\n\t\t", (CharSequence[]) lines);
        return body + (body.endsWith(";") ? "" : ";") + (!body.contains("return ") && !body.contains("throw ") ? ";return null;" : "") + "\n\t}"
                + "\n\n\tpublic void print(Object o) { System.out.print(o.toString()); }\n" +
                "\tpublic void println(Object o) { print(o.toString() + \"\\n\"); }\n" +
                "\tpublic void printErr(Object o) { System.err.print(o.toString()); }\n" +
                "\tpublic void printErrln(Object o) { printErr(o.toString() + \"\\n\"); }\n" +
                "\tprivate MessageEvent e;\n\n" +
                "\tpublic " + f.getName().replace(".java", "") + "(MessageEvent e)\n\t{\n" +
                "\t\tthis.e = e;\n\t}\n}";
    }



}
