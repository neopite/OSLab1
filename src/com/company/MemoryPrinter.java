package com.company;

import java.util.Arrays;

public class MemoryPrinter {
    public static void printMemory(Allocator allocator){
        byte [] arr = allocator.getMem();
        int begin = 0 ;
        int counter = 0 ;
        while(true){
            byte [] header = Arrays.copyOfRange(arr,begin,begin+Allocator.getHeader());
            System.out.println("  Header Ind : " + begin+   "  Free : " + !Utils.byteToBool(arr[begin]) +
                    " . Prev Block size : " + allocator.getPrevBlockSize(header) +
                    " . Curr Block size : " + allocator.getCurrBlockSize(header)
                    );
            if(allocator.getCurrBlockSize(header)<8 || begin+8 + allocator.getCurrBlockSize(header) + 1 >= allocator.getMem().length){
                break;
            }else begin += 8 + allocator.getCurrBlockSize(header) + 1;
        }
    }
}
