import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class TrieTest {

    @Test
    public void findTest() {
        HashMap<Character, Trie.Node> map = new HashMap<>();
        ArrayList<Character> l = new ArrayList<>();
        l.add('b');
        l.add('f');
        l.add('h');
        map.put('a', new Trie.Node(l));
        Trie tr = new Trie(map);
        Assert.assertTrue(tr.find("ab"));
        Assert.assertTrue(tr.find("af"));
        Assert.assertTrue(tr.find("ah"));
        Assert.assertFalse(tr.find("ak"));
        //check if 'a' somehow is a word
        Assert.assertFalse(tr.find("a"));
        Assert.assertFalse(tr.find(""));
    }

    @Test
    public void addTest() {
        Trie tri = new Trie();
        tri.add("asd");
        tri.add("asdf");
        Assert.assertTrue(tri.find("asd"));
        Assert.assertFalse(tri.find("as"));
        Assert.assertFalse(tri.find("asr"));
        Assert.assertTrue(tri.find("asdf"));
        tri.add("aser");
        tri.add("aerr");
        //check that 'a' have 2 children
        Assert.assertEquals(2, tri.findNode("a").children.size());
        Assert.assertFalse(tri.findNode("a").isWord);
        Assert.assertTrue(tri.findNode("aerr").isWord);
        tri.add("");
        Assert.assertFalse(tri.find(""));
    }

    @Test
    public void deleteTest() {
        Trie trie = new Trie();
        trie.add("asd#$%@");
        trie.delete("asd#$%@");
        Assert.assertEquals("", trie.toString());
        //make sure that all useless nodes are deleted
        Assert.assertNull(trie.findNode("a"));
        trie.delete("");
        //what if we delete single letter string?
        trie.add("o");
        trie.delete("o");
        Assert.assertNull(trie.findNode("o"));
        //what if we delete longer string?
        trie.add("zxcvb");
        trie.add("zxcv");
        trie.delete("zxcvb");
        Assert.assertTrue(trie.find("zxcv"));
        Assert.assertFalse(trie.find("zxcvb"));
        //what if we delete shorter string?
        trie.add("qwerty");
        trie.add("qwert");
        trie.delete("qwert");
        Assert.assertTrue(trie.find("qwerty"));
        Assert.assertFalse(trie.find("qwert"));
        //what if we delete not existing string?
        String temp = trie.toString();
        trie.delete("not existing string");
        Assert.assertEquals(temp, trie.toString());
    }

    @Test public void findAllTest() {
        Trie tr = new Trie();
        tr.add("asdfg");
        tr.add("asdcvbnm7&89e$3#n/");
        tr.add("asd.:'[][(*@$#!^*);");
        tr.add("bsd");
        tr.add("asdftyuio");
        HashSet set = new HashSet();
        set.add("asdfg");
        set.add("asdcvbnm7&89e$3#n/");
        set.add("asd.:'[][(*@$#!^*);");
        set.add("asdftyuio");
        Assert.assertEquals(set, tr.findAllWithPrefix("asd"));
    }
}
