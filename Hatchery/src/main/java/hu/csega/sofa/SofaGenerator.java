package hu.csega.sofa;

import hu.csega.editors.common.resources.FileResourceAdapter;

import java.io.*;

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

        tree.generate();

        System.out.println("Used: " + tree.getUsed());

        String test = "Fox jumps abc-123 over lazy dog.";
        SofaResult result = new SofaResult();
        tree.analyze(test, result);

        System.out.println("------");
        System.out.println(test);
        System.out.println("Number of words: " + result.numberOfWords);
        System.out.println("Number of accepted words: " + result.numberOfAcceptedWords);
    }

}
