import java.util.*;

public final class Trie {

    Trie() {
        root.children = new HashMap<>();
        root.isWord = false;
    }

    Trie(HashMap<Character, Node> map) {
        root.children = map;
        root.isWord = false;
    }

    static class Node {
        Map<Character, Node> children;
        boolean isWord;

        Node(boolean isW) {
            children = new HashMap<>();
            isWord = isW;
        }

        Node(List<Character> list) {
            children = new HashMap<>();
            isWord = false;
            for (Character ch: list) {
                children.put(ch, new Node(true));
            }
        }
    }

    private Node root = new Node(false);

    public boolean find(String str) {
        Node it = root;
        for (int i = 0; i < str.length(); i++) {
            it = it.children.get(str.charAt(i));
            if (it == null) return false;
        }
        return it.isWord;
    }

    public Node findNode(String str) {
        Node it = root;
        for (int i = 0; i < str.length(); i++) {
            it = it.children.get(str.charAt(i));
            if (it == null) return null;
        }
        return it;
    }

    public void add(String str) {
        Node it = root;
        for (int i = 0; i < str.length(); i++) {
            it.children.putIfAbsent(str.charAt(i), new Node(false));
            it = it.children.get(str.charAt(i));
        }
        if (!str.isEmpty()) it.isWord = true;
    }

    //delete all useless nodes, not only last one
    public void delete(String str) {
        if (!str.isEmpty()) {
            if (str.length() == 1) {
                root.children.remove(str.charAt(0));
            } else {
                int i = str.length();
                Node it = findNode(str.substring(0, i));
                //going from the end to the beginning of the deleted string
                while (i > 0) {
                    //if we haven't found this string we have nothing to delete so we stop
                    if (it != null) {
                        //two cases that allows us to delete this char: first for the end of the string
                        //that mustn't have a child to be deleted
                        //second for any other char in the string before the end
                        //Obviously, it has at least one child, but no more, and shouldn't be a word.
                        if (i == str.length() && it.children.keySet().size() == 0
                                || i != str.length() && it.children.keySet().size() == 1
                                && !it.isWord) {
                            //we made sure that we can delete this char, so we going to the next one
                            it = findNode(str.substring(0, --i));
                        } else {
                            if (i == str.length()) {
                                //if the end of the string have a child we mark it as not a word and stop
                                it.isWord = false;
                                break;
                            }
                            //if char which contains useful link has been found, we delete
                            //the part of the line that begins after and stop
                            it.children.remove(str.charAt(i));
                            break;
                        }
                        if (i == 1) {
                            //if we reached the beginning of the string and it haven't got a useful link
                            //we delete it from root and stop
                            root.children.remove(str.charAt(0));
                            break;
                        }
                    } else {
                        break;
                    }
                }
            }
        }
    }

    interface InnerFun {
        void findStrWithDFS(Node node, StringBuilder str);
    }

    public Set<String> findAllWithPrefix(String str) {
        Node it = findNode(str);
        if (it == null) return null;
        Set<String> res = new HashSet<>();
        StringBuilder sb = new StringBuilder(str);

        InnerFun fun = new InnerFun() {
            @Override
            public void findStrWithDFS(Node node, StringBuilder str) {
                if (node.isWord) {
                    res.add(str.toString());
                }
                for (Character ch : node.children.keySet()) {
                    findStrWithDFS(node.children.get(ch), str.append(ch));
                    str.deleteCharAt(str.length() - 1);
                }
            }
        };

        for (Character ch : it.children.keySet()) {
            fun.findStrWithDFS(it.children.get(ch), sb.append(ch));
            sb.deleteCharAt(sb.length() - 1);
        }

        return res;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Character ch: root.children.keySet()) {
            sb.append(findAllWithPrefix(ch.toString()));
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Trie trie = (Trie) o;
        return Objects.equals(root, trie.root);
    }

    @Override
    public int hashCode() {
        return Objects.hash(root);
    }
}


