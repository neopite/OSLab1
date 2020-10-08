package com.company;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        Allocator allocator = new Allocator(256);
        System.out.println("0===================================");
        int index0=allocator.memAlloc(15);
        MemoryPrinter.printMemory(allocator);
        System.out.println("1===================================");
        int index = allocator.memAlloc(60);
        MemoryPrinter.printMemory(allocator);
        System.out.println("2===================================");
        int index2 = allocator.memAlloc(12);
        MemoryPrinter.printMemory(allocator);
        System.out.println("3===================================");
        allocator.memAlloc(12);
        MemoryPrinter.printMemory(allocator);
        System.out.println("4===================================");
        int index4 = allocator.memRealloc(index, 100);
        MemoryPrinter.printMemory(allocator);
        System.out.println("5===================================");
       allocator.memFree(index2);
        MemoryPrinter.printMemory(allocator);
        System.out.println("6===================================");
        allocator.memFree(index0);
        allocator.memFree(115);
        allocator.memFree(136);
        MemoryPrinter.printMemory(allocator);


/*
        allocator.memAlloc(23);
        allocator.memAlloc(23);
        allocator.memAlloc(60);
        allocator.memAlloc(25);
        allocator.memAlloc(27);
        allocator.memFree(66);
        allocator.memAlloc(15);
        allocator.memAlloc(15);
        allocator.memAlloc(15);
        MemoryPrinter.printMemory(allocator);

 */

    }
}


    /*TODO  1.Simplify code
            2. Write memRealloc
            3. Fix memFree with first and last block

     */

