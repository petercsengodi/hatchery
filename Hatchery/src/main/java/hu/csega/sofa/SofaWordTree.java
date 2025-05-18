package hu.csega.sofa;

import java.util.Set;
import java.util.TreeSet;

public class SofaWordTree {

    public static final int STOP = 0;
    public static final int ACCEPT = 1;
    public static final int DECLINE = 0;

    private int currentLength = 10_000;
    private int[] data = new int[currentLength];
    private int used = 0;

    private Set<SofaTreeNode> root = new TreeSet<>();

    public int getUsed() {
        return used;
    }

    public void addWord(String word) {
        Set<SofaTreeNode> set = root;
        int wordLength = word.length();

        for(int pos = 0; pos < wordLength; pos++) {
            boolean found = false;
            int code = (int) word.charAt(pos);

            for(SofaTreeNode node : set) {
                if(node.code == code) {
                    set = node.children;
                    found = true;
                    break;
                }
            }

            if(!found) {
                SofaTreeNode newNode = new SofaTreeNode(code);
                set.add(newNode);
                set = newNode.children;
            }
        }

        if(set.isEmpty() || set.iterator().next().code != STOP)
            set.add(new SofaTreeNode());

    }

    public void generate() {
        generate(root);
    }

    private void generate(Set<SofaTreeNode> set) {
        int lengthPos = -1;

        boolean first = true;
        for(SofaTreeNode node : set) {
            if(first) {
                first = false;

                if(node.code == STOP) {
                    write(ACCEPT);
                    lengthPos = write(set.size() - 1);
                    continue;
                } else {
                    write(DECLINE);
                    lengthPos = write(set.size());
                    // No "continue", we go on with the first letter.
                }
            }

            write(node.code);
            write(STOP); // Will be overwritten later.
        }

        int jumpPos = lengthPos;
        for(SofaTreeNode node : set) {
            if(node.code == STOP)
                continue;

            jumpPos += 2;
            writeIntoPos(jumpPos, used);
            generate(node.children);
        }
    }

    private int write(int i) {
        if(used >= currentLength)
            doubleTheAllocatedSize();
        int pos = used++;
        data[pos] = i;
        return pos;
    }

    private void writeIntoPos(int pos, int i) {
        data[pos] = i;
    }

    private void doubleTheAllocatedSize() {
        int newLength = currentLength * 2;
        int[] tmp = new int[newLength];
        System.arraycopy(data, 0, tmp, 0, currentLength);
        data = tmp;
        currentLength = newLength;
    }

    public void analyze(String test, SofaResult result) {
        result.numberOfWords = result.numberOfAcceptedWords = 0;
        if(test == null)
            return;

        test = test.trim();
        if(test.isEmpty())
            return;

        int len = test.length();
        int pos = 0;
        int numberOfLetters = 0;
        int dataPos = 0;
        boolean stillAcceptable = true;

        while(pos < len + 1) {
            char c = (pos < len ? test.charAt(pos) : 0);
            pos++;

            if(Character.isLetter(c)) {
                numberOfLetters++;
                if(stillAcceptable) {
                    int code = (int) Character.toLowerCase(c);
                    int numberOfChoices = data[dataPos + 1];
                    int offset = dataPos + 2;

                    int found = find(code, offset, numberOfChoices);
                    if(found < 1)
                        stillAcceptable = false;
                    else
                        dataPos = found;
                }
            } else {
                if(numberOfLetters > 0) {
                    if(stillAcceptable && data[dataPos] == ACCEPT)
                        result.numberOfAcceptedWords++;
                    result.numberOfWords++;
                    numberOfLetters = 0;
                    dataPos = 0;
                    stillAcceptable = true;
                }
            }
        }
    }

    /** Binary search */
    // FIXME make it a binary search
    private int find(int code, int offset, int numberOfChoices) {
        int endPos = offset + numberOfChoices * 2;
        while(offset < endPos) {
            if(code == data[offset])
                return data[offset + 1];
            offset += 2;
        }

        return -1;
    }
}
