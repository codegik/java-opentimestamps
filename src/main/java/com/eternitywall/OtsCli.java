package com.eternitywall; /**
 * Created by luca on 25/02/2017.
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.Date;
import java.util.logging.Logger;

public class OtsCli {

    private static Logger log = Logger.getLogger(OtsCli.class.getName());
    private static String title = "OtsCli";
    private static String version = "1.0";

    public static void main(String[] args) {


        if (args == null || args.length == 0) {
            showHelp();
            return;
        }

        switch (args[0]) {
            case "info":
            case "i":
                if (args.length != 2) {
                    System.out.print("Show information on a timestamp given as argument.\n");
                    System.out.print(title + " info: bad options number ");
                    break;
                }
                info(args[1]);
                break;
            case "stamp":
            case "s":
                if (args.length != 2) {
                    System.out.print("Create timestamp with the aid of a remote calendar.\n");
                    System.out.print(title + ": bad options number ");
                    break;
                }
                stamp(args[1]);
                break;
            case "verify":
            case "v":
                if (args.length != 2) {
                    System.out.print("Verify the timestamp attestations given as argument.\n");
                    System.out.print(title + ": bad options number ");
                    break;
                }
                verify(args[1]);
                break;
            case "upgrade":
            case "u":
                if (args.length != 2) {
                    System.out.print("Upgrade remote calendar timestamps to be locally verifiable.\n");
                    System.out.print(title + ": bad options number ");
                    break;
                }
                upgrade(args[1]);
                break;
            case "--version":
            case "-V":
                System.out.print("Version: " + title + " v." + version + '\n');
                break;
            case "--help":
            case "-h":
                showHelp();
                break;
            default:
                System.out.print(title + ": bad option: " + args[0]);
        }
    }


    public static void info (String argsOts) {
        try {
            Path pathOts = Paths.get(argsOts);
            byte[] byteOts = Files.readAllBytes(pathOts);
            String infoResult = OpenTimestamps.info(byteOts);
            System.out.print(infoResult);
        } catch (IOException e) {
            e.printStackTrace();
            log.severe("No valid file");
        }
    }

    public static void stamp (String argsFile) {
        FileInputStream fis = null;
        try {

            File file = new File(argsFile);
            fis = new FileInputStream(file);
            byte[] infoResult = OpenTimestamps.stamp(fis);
            System.out.print(Utils.bytesToHex(infoResult));

        } catch (IOException e) {
            e.printStackTrace();
            log.severe("No valid file");
        } finally {
            try {
                fis.close();
            }catch  (IOException e) {
                log.severe("No valid file");
            }
        }
    }

    public static void verify (String argsOts) {
        FileInputStream fis = null;
        try {

            Path pathOts = Paths.get(argsOts);
            byte[] byteOts = Files.readAllBytes(pathOts);

            String argsFile = argsOts.replace(".ots","");
            File file = new File(argsFile);
            fis = new FileInputStream(file);
            String verifyResult = OpenTimestamps.verify(byteOts,fis);
            if(verifyResult==null){
                System.out.print("Pending or Bad attestation");
            }else {
                System.out.print("Success! Bitcoin attests data existed as of "+(new Date(Integer.valueOf(verifyResult) * 1000)));
            }

        } catch (IOException e) {
            e.printStackTrace();
            log.severe("No valid file");
        } finally {
            try {
                fis.close();
            }catch  (IOException e) {
                log.severe("No valid file");
            }
        }
    }

    public static void upgrade (String argsOts) {
        try {
            Path pathOts = Paths.get(argsOts);
            byte[] byteOts = Files.readAllBytes(pathOts);
            byte[] upgradeResult = OpenTimestamps.upgrade(byteOts);
            System.out.print(Utils.bytesToHex(upgradeResult));
        } catch (IOException e) {
            e.printStackTrace();
            log.severe("No valid file");
        }
    }

    public static void showHelp() {
        System.out.print(
                "Usage: " + title + " [options] {stamp,s,upgrade,u,verify,v,info} [arguments]\n\n" +
                "Subcommands:\n" +
                "s, stamp FILE       \tCreate timestamp with the aid of a remote calendar, the output receipt will be saved with .ots\n" +
                "S, multistamp FILES       \tCreate timestamp with the aid of a remote calendar, the output receipt will be saved with .ots\n" +
                "i, info FILE_OTS \tShow information on a timestamp.\n" +
                "v, verify FILE_OTS\tVerify the timestamp attestations, expect original file present in the same directory without .ots\n" +
                "u, upgrade FILE_OTS\tUpgrade remote calendar timestamps to be locally verifiable.\n\n" +
                "Options:\n" +
                        "-V, --version         \tprint " + title + " version.\n" +
                        "-h, --help         \tprint this help.\n" +
                        "\nLicense: LGPL."
        );
    }

}