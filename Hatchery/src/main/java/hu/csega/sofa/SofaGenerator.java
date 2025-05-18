package hu.csega.sofa;

import hu.csega.editors.common.resources.FileResourceAdapter;

import java.io.*;
import java.util.Random;

public class SofaGenerator {

    public static final String SOFA_PROJECT = "sofa";

    public static void main(String[] args) throws Exception {
        FileResourceAdapter resourceAdapter = new FileResourceAdapter("Hatchery");
        File input = new File(resourceAdapter.projectRoot() + "demo" + File.separator + SOFA_PROJECT + File.separator + "input.txt");
        File output = new File(resourceAdapter.projectRoot() + "demo" + File.separator + SOFA_PROJECT + File.separator + "output.bin");

        SofaWordTree tree = new SofaWordTree();

        InputStream inputStream = new FileInputStream(input);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;

            while((line = reader.readLine()) != null) {
                line = line.trim();
                if(line.isEmpty() || line.charAt(0) == '#')
                    continue;

                tree.addWord(line);
            }
        }

        String pattern = "abcdefghijklmnopqrstuvwxyz";
        int patternLength = pattern.length();
        Random rnd = new Random(System.currentTimeMillis());

        for(int i = 0; i < 20_000; i++) {
            int len = rnd.nextInt(12) + 2;
            StringBuilder b = new StringBuilder();
            for(int j = 0; j < len; j++) {
                char c = pattern.charAt(rnd.nextInt(patternLength));
                b.append(c);
            }
            tree.addWord(b.toString());
        }


        tree.generate();

        System.out.println("Used: " + tree.getUsed());

        String test = "Fox jumps abc-123 over lazy dog.";
        SofaResult result = new SofaResult();
        tree.analyze(test, result, true);

        System.out.println("------");
        System.out.println(test);
        System.out.println("Number of words: " + result.numberOfWords);
        System.out.println("Number of accepted words: " + result.numberOfAcceptedWords);
        System.out.println();

        String[] sentences = new String[100];
        for(int k = 0; k < sentences.length; k++) {
            StringBuilder b = new StringBuilder();
            for(int w = 0; w < 20; w++) {
                b.append(' ');
                int len = rnd.nextInt(12) + 2;
                for (int j = 0; j < len; j++) {
                    char c = pattern.charAt(rnd.nextInt(patternLength));
                    b.append(c);
                }
            }
            sentences[k] = b.toString();
        }

        System.out.println("Test sentences are generated.");
        System.out.println();

        long start = System.currentTimeMillis();

        System.out.println();
        for(int i = 0; i < 10000; i++) {
            if (i % 1000 == 999)
                System.out.print('.');
            if (i == 9999)
                System.out.println();
            for(int k = 0; k < sentences.length; k++) {
                String s = sentences[k];
                tree.analyze(s, result, false);
                if (i == 9999999)
                    System.out.println(s + ' ' + result.numberOfAcceptedWords + '/'  + result.numberOfWords);
            }
        }

        long end = System.currentTimeMillis();
        System.out.println();
        System.out.println("------");
        System.out.println(((end - start) / 1000.0) + " secs.");

        System.out.println();
        System.out.println("======= Same with binary search ========");
        System.out.println();

        start = System.currentTimeMillis();

        System.out.println();
        for(int i = 0; i < 10000; i++) {
            if (i % 1000 == 999)
                System.out.print('.');
            if (i == 9999)
                System.out.println();
            for(int k = 0; k < sentences.length; k++) {
                String s = sentences[k];
                tree.analyze(s, result, true);
                if (i == 9999999)
                    System.out.println(s + ' ' + result.numberOfAcceptedWords + '/'  + result.numberOfWords);
            }
        }

        end = System.currentTimeMillis();
        System.out.println();
        System.out.println("------");
        System.out.println(((end - start) / 1000.0) + " secs.");
    }

}
