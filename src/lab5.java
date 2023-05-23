/*
Names: Sadie Fisher and Angelika Canete
Section: 01
Description: This program simulates an environment to run MIPS programs.
 */


import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class lab5 {
    public static void main(String[] args) throws FileNotFoundException {
        File file = new File(args[0]);

        Hashtable<String, Integer> labels = utility.findLabels(file);

        ArrayList<Instruction> insts = utility.getInstructions(file, labels);

        MipsSimulator mipsim = new MipsSimulator(insts);

        // SCRIPT MODE
        File script = new File(args[1]);

        if (args.length == 3) {
            mipsim.setGhr_size(Integer.parseInt(args[2]));
        }
        Scanner scnr = new Scanner(script);

        while ( scnr.hasNext() ) {
            String cmd = scnr.nextLine();
            System.out.print("\n" + "mips> " + cmd);

            if (cmd.charAt(0) == 'h') {
                MipsSimulator.h();
            }
            else if (cmd.charAt(0) == 'd') {
                mipsim.d();
            }
            else if (cmd.charAt(0) == 's') {
                if (cmd.length() > 1) {
                    String[] line = cmd.split(" ");
                    int numsteps = Integer.parseInt(line[1]);
                    mipsim.snum(numsteps);
                }
                else {
                    mipsim.s();
                    System.out.print("\n        1 instruction(s) executed");
                }
            }
            else if (cmd.charAt(0) == 'r') {
                mipsim.r();
            }
            else if (cmd.charAt(0) == 'm') {
                String[] line = cmd.split(" ");
                int num1 = Integer.parseInt(line[1]);
                int num2 = Integer.parseInt(line[2]);
                mipsim.m(num1, num2);
            }
            else if (cmd.charAt(0) == 'c') {
                mipsim.c();
                System.out.println("\n        Simulator reset");
            }
            else if (cmd.charAt(0) == 'b') {
                mipsim.b();
            }
            else if (cmd.charAt(0) == 'q') {
                break;
            }


//        // INTERACTIVE MODE
//        if (args.length == 1) {
//            Scanner scnr = new Scanner(System.in);
//
//            while (true) {
//                System.out.println("mips> ");
//
//                String cmd = scnr.nextLine();
//
//                if (cmd.charAt(0) == 'h') {
//                    MipsSimulator.h();
//                }
//                else if (cmd.charAt(0) == 'd') {
//                    mipsim.d();
//                }
//                else if (cmd.charAt(0) == 's') {
//                    if (cmd.length() > 1) {
//                        String[] line = cmd.split(" ");
//                        int numsteps = Integer.parseInt(line[1]);
//                        mipsim.snum(numsteps);
//                        System.out.print("\n");
//                    }
//                    else {
//                        mipsim.s();
//                        System.out.println("        1 instruction(s) executed");
//                    }
//                }
//                else if (cmd.charAt(0) == 'r') {
//                    mipsim.r();
//                }
//                else if (cmd.charAt(0) == 'm') {
//                    String[] line = cmd.split(" ");
//                    int num1 = Integer.parseInt(line[1]);
//                    int num2 = Integer.parseInt(line[2]);
//                    mipsim.m(num1, num2);
//                }
//                else if (cmd.charAt(0) == 'c') {
//                    mipsim.c();
//                    System.out.println("        Simulator reset");
//                }
//                else if (cmd.charAt(0) == 'q') {
//                    break;
//                }
//            }
//        }


        }


    }




}
