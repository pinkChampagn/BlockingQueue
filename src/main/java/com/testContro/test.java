package com.testContro;

import org.junit.Test;

import java.util.Arrays;
import java.util.concurrent.Semaphore;

public class test {
    //Semaphore

    public TreeNode recoverFromPreorder(String S) {
        char[]str=S.toCharArray();
        char[]child= Arrays.copyOf(str,str.length);
        while (child.length>0){
            int i=0;
            if((int)child[i]!=45){

            }
        }


        return null;
    }
    @Test
    public void test(){
        System.out.print((int)'-');
    }
}
class TreeNode {
      int val;
      TreeNode left;
      TreeNode right;
      TreeNode(int x) { val = x; }
  }
