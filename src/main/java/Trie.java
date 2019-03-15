import java.util.*;

public final class Trie {

    Trie() {
        root.children = new HashMap<>();
        root.isWord = false;
    }

    //constructor was made in purpose of testing
    Trie(Character ch, HashSet<Character> set) {
        root.putChildIfAbsent(ch);
        Node it = root.getChild(ch);
        for (Character el: set) {
            it.putChildIfAbsent(el);
            it.getChild(el).setWord(true);
        }
        root.setWord(false);
        it.setWord(false);
    }

    static class Node {
        private Map<Character, Node> children;
        private boolean isWord;

        Node() {
            children = new HashMap<>();
            isWord = false;
        }

        public Set<Character> getChildrenNames() {
            return children.keySet();
        }

        public int getNumberOfChildren() {
            return children.size();
        }

        public Node getChild(Character ch) {
            return children.get(ch);
        }

        private void putChildIfAbsent(Character ch) {
            children.putIfAbsent(ch, new Node());
        }

        private void removeChild(Character ch) {
            children.remove(ch);
        }

        public boolean isWord() {
            return isWord;
        }

        private void setWord(boolean bool) {
            isWord = bool;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
            return isWord == node.isWord &&
                    Objects.equals(children, node.children);
        }

        @Override
        public int hashCode() {
            return Objects.hash(children, isWord);
        }
    }

    private Node root = new Node();

    public boolean find(String str) {
        Node it = root;
        for (int i = 0; i < str.length(); i++) {
            it = it.getChild(str.charAt(i));
            if (it == null) return false;
        }
        return it.isWord();
    }

    //can return null
    public Node findNode(String str) {
        Node it = root;
        for (int i = 0; i < str.length(); i++) {
            it = it.getChild(str.charAt(i));
            if (it == null) return null;
        }
        return it;
    }

    public void add(String str) {
        Node it = root;
        for (int i = 0; i < str.length(); i++) {
            it.putChildIfAbsent(str.charAt(i));
            it = it.getChild(str.charAt(i));
        }
        if (!str.isEmpty()) it.setWord(true);
    }

    //delete all useless nodes, not only last one
    public void delete(String str) {
        if (!str.isEmpty()) {
            if (str.length() == 1) {
                root.removeChild(str.charAt(0));
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
                        if (i == str.length() && it.getNumberOfChildren() == 0
                                || i != str.length() && it.getNumberOfChildren() == 1
                                && !it.isWord()) {
                            //we made sure that we can delete this char, so we going to the next one
                            it = findNode(str.substring(0, --i));
                        } else {
                            if (i == str.length()) {
                                //if the end of the string have a child we mark it as not a word and stop
                                it.setWord(false);
                                break;
                            }
                            //if char which contains useful link has been found, we delete
                            //the part of the line that begins after and stop
                            it.removeChild(str.charAt(i));
                            break;
                        }
                        if (i == 1) {
                            //if we reached the beginning of the string and it haven't got a useful link
                            //we delete it from root and stop
                            root.removeChild(str.charAt(0));
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

    //can return null
    public Set<String> findAllWithPrefix(String str) {
        Node it = findNode(str);
        if (it == null) return null;
        Set<String> res = new HashSet<>();
        StringBuilder sb = new StringBuilder(str);

        InnerFun fun = new InnerFun() {
            @Override
            public void findStrWithDFS(Node node, StringBuilder str) {
                if (node.isWord()) {
                    res.add(str.toString());
                }
                for (Character ch : node.getChildrenNames()) {
                    findStrWithDFS(node.getChild(ch), str.append(ch));
                    str.deleteCharAt(str.length() - 1);
                }
            }
        };

        if (it.isWord()) res.add(str);
        for (Character ch : it.getChildrenNames()) {
            fun.findStrWithDFS(it.getChild(ch), sb.append(ch));
            sb.deleteCharAt(sb.length() - 1);
        }

        return res;
    }

    //can return empty set, can't return null
    public Set<String> findAll() {
        Node it = root;
        if (root.getChildrenNames().isEmpty()) return new HashSet<>();
        Set<String> res = new HashSet<>();

        InnerFun fun = new InnerFun() {
            @Override
            public void findStrWithDFS(Node node, StringBuilder str) {
                if (node.isWord()) {
                    res.add(str.toString());
                }
                for (Character ch : node.getChildrenNames()) {
                    findStrWithDFS(node.getChild(ch), str.append(ch));
                    str.deleteCharAt(str.length() - 1);
                }
            }
        };

        for (Character ch : it.getChildrenNames()) {
            StringBuilder sb = new StringBuilder();
            sb.append(ch);
            fun.findStrWithDFS(it.getChild(ch), sb);
        }

        return res;
    }

    //every set of root children surrounded by []
    //every string surrounded by ()
    //one space between strings and sets of children
    //if root hasn't got children then returns "[]"
    //strings are sorted
    //for example: trie have "sea", "see", "bus", it will appear as [(bus)] [(sea) (see)]
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Set<Character> rootSet = root.getChildrenNames();
        if (rootSet.isEmpty()) return "[]";
        TreeSet<Character> sortedRootSet = new TreeSet<>(rootSet);
        for (Character ch: sortedRootSet) {
            sb.append("[");
            //if root has at least one child, it has at least one word.
            TreeSet<String> sortedStringSet = new TreeSet<>(findAllWithPrefix(ch.toString()));
            for(String str: sortedStringSet) {
                sb.append("(").append(str).append(") ");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append("] ");
        }
        sb.deleteCharAt(sb.length() - 1);
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


