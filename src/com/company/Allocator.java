package com.company;

import java.util.Arrays;

public class Allocator {
    private final byte[] mem;
    private static final int HEADER = 9;
    private final byte[] arrOfZeros = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0};

    public Allocator(int allocSize) {
        this.mem = new byte[allocSize];
        byte[] beginHeader = createHeader(allocSize - HEADER, 0);  //create basic header
        System.arraycopy(beginHeader, 0, this.mem, 0, 9);       //put header in memory
    }

    public byte[] createHeader(int sizeOfBlock, int prevBlockSize) {
        byte[] firstByte = new byte[]{Utils.boolToByte(false)};
        byte[] blockSizeOfPrevBlock = Utils.intToByteArray(prevBlockSize);
        byte[] blockSizeOfCurrentBlock = Utils.intToByteArray(sizeOfBlock);
        byte[] header = new byte[firstByte.length + blockSizeOfCurrentBlock.length + blockSizeOfPrevBlock.length];
        System.arraycopy(firstByte, 0, header, 0, 1);
        System.arraycopy(blockSizeOfPrevBlock, 0, header, 1, blockSizeOfPrevBlock.length);
        System.arraycopy(blockSizeOfCurrentBlock, 0, header, blockSizeOfPrevBlock.length + 1, blockSizeOfCurrentBlock.length);
        return header;
    }

    public Integer memAlloc(int currSize) {
        int size = offsetSize(currSize);
        int startOfFreeBlock = findNotBusyBlock(size);
        if (size > this.mem.length - HEADER || startOfFreeBlock + size + HEADER > mem.length) {                                                         // check if this header can be assigned in memory
            return null;
        }
        byte[] currentHeader = Arrays.copyOfRange(mem, startOfFreeBlock - HEADER, startOfFreeBlock);
        int sizeOfFoundedBlock = getCurrBlockSize(currentHeader) - HEADER;
        byte[] newSizeBlock = Utils.intToByteArray(size);
        byte[] nextHeader = createHeader(sizeOfFoundedBlock - size, size);                               // create next header for free block
        mem[startOfFreeBlock - HEADER] = 1;                                                                         //put first bite of header(block is occupied)
        System.arraycopy(newSizeBlock, 0, mem, startOfFreeBlock - HEADER + 5, newSizeBlock.length);              // put new currSize of occupied block
        System.arraycopy(nextHeader, 0, mem, startOfFreeBlock + size, nextHeader.length);             // assign header to the next free block (if it exist)
        byte[] rightNeighbor = getRightNHead(startOfFreeBlock - HEADER, getCurrBlockSize(currentHeader));            //set to right neighbor new prevSizeBlock(when we free block and try to use this free block and we must to set new prevBlockSize to right neighbor)
        if (rightNeighbor != null && rightNeighbor[0] == 1) {
            byte[] prevValForRightNeighb = Utils.intToByteArray(sizeOfFoundedBlock - size);
            System.arraycopy(prevValForRightNeighb, 0, mem, startOfFreeBlock + getCurrBlockSize(currentHeader) + 1, 4);
        }
        return startOfFreeBlock - HEADER;        // return start index of block
    }

    public int memRealloc(int index, int size) {
        byte[] currentHeader = Arrays.copyOfRange(mem, index, index + HEADER);
        int sizeCurrentHeader = getCurrBlockSize(currentHeader);
        if (size >= sizeCurrentHeader) {
            memFree(index);
            return memAlloc(size);
        } else if (size + HEADER < sizeCurrentHeader) {
            byte[] firstHeader = createHeader(sizeCurrentHeader - size - HEADER, getPrevBlockSize(currentHeader));
            firstHeader[0] = 1;
            byte[] secondHeader = createHeader(size, getCurrBlockSize(firstHeader));
            System.arraycopy(firstHeader, 0, mem, index, firstHeader.length);
            System.arraycopy(secondHeader, 0, mem, index + HEADER + getCurrBlockSize(firstHeader), secondHeader.length);
            System.arraycopy(Arrays.copyOfRange(secondHeader, 5, 9), 0, mem,
                    index + HEADER * 2 + getCurrBlockSize(firstHeader) + getCurrBlockSize(secondHeader) + 1, 4
            );
            return index + HEADER + getCurrBlockSize(firstHeader);
        }
        return -1;
    }


    public void memFree(int startIndexOfHeader) {
        mem[startIndexOfHeader] = 0;
        int in = uniteLeft(startIndexOfHeader);
        if (in != -1) {
            uniteRight(in);
        } else uniteRight(startIndexOfHeader);
    }

    private int uniteLeft(int headIndex) {
        byte[] currH = Arrays.copyOfRange(mem, headIndex, headIndex + HEADER);
        byte[] leftH = getLeftNHead(headIndex, getPrevBlockSize(currH));
        byte[] rightH = getRightNHead(headIndex, getCurrBlockSize(currH));
        if (leftH != null && leftH[0] == 0 && currH[0] == 0) {
            byte[] newSizeOfBlock = Utils.intToByteArray(getCurrBlockSize(leftH) + getCurrBlockSize(currH) + HEADER);
            System.arraycopy(newSizeOfBlock, 0, mem, headIndex - getPrevBlockSize(currH) - HEADER + 5, newSizeOfBlock.length);
            if (rightH != null) {
                System.arraycopy(newSizeOfBlock, 0, mem, headIndex + HEADER + getCurrBlockSize(currH) + 1, newSizeOfBlock.length);
            }
            deleteHeader(headIndex);
            return headIndex - getPrevBlockSize(currH) - HEADER;
        }
        return -1;
    }

    private void uniteRight(int headIndex) {
        byte[] currH = Arrays.copyOfRange(mem, headIndex, headIndex + HEADER);
        byte[] rightH = getRightNHead(headIndex, getCurrBlockSize(currH));
        if (rightH != null && rightH[0] == 0 && currH[0] == 0) {
            currH = Arrays.copyOfRange(mem, headIndex, headIndex + HEADER);
            rightH = getRightNHead(headIndex, getCurrBlockSize(currH));
            byte[] newSize = Utils.intToByteArray(getCurrBlockSize(currH) + getCurrBlockSize(rightH) + HEADER);
            System.arraycopy(new byte[]{Utils.boolToByte(false)}, 0, mem, headIndex, 1);
            System.arraycopy(newSize, 0, mem, headIndex + 5, newSize.length);
            byte[] rightNN = getRightNHead(headIndex + getCurrBlockSize(currH) + HEADER, getCurrBlockSize(rightH));
            if (rightNN != null) {
                System.arraycopy(newSize, 0, mem, headIndex + HEADER * 2 + getCurrBlockSize(currH) + getCurrBlockSize(rightH) + 1, newSize.length);   // set to the right of right neighbor prev blockSize
            }
            deleteHeader(headIndex + getCurrBlockSize(currH) + HEADER);
        }
    }


    private void deleteHeader(int startIndexOfHeader) {
        System.arraycopy(arrOfZeros, 0, mem, startIndexOfHeader, arrOfZeros.length);
    }

    byte[] getLeftNHead(int startHeaderIndex, int prevBlockSize) {
        if (startHeaderIndex == 0) {
            return null;
        }
        return Arrays.copyOfRange(mem, startHeaderIndex - prevBlockSize - HEADER, startHeaderIndex - prevBlockSize);
    }

    byte[] getRightNHead(int startHeaderIndex, int currentBlockSize) {
        if (startHeaderIndex + HEADER + currentBlockSize >= mem.length) {
            return null;
        }
        return Arrays.copyOfRange(mem, startHeaderIndex + currentBlockSize + HEADER, startHeaderIndex + currentBlockSize + HEADER * 2);
    }

    //return start of empty block(first byte after header)
    private int findNotBusyBlock(int size) {
        int beginOfHeader = 0;
        while (true) {
            int currBlockSize = getCurrBlockSize(Arrays.copyOfRange(mem, beginOfHeader, beginOfHeader + HEADER));
            if (mem[beginOfHeader] == 0 && currBlockSize >= size + HEADER) {
                return beginOfHeader + HEADER;
            } else beginOfHeader += HEADER + currBlockSize;
        }
    }


    int getPrevBlockSize(byte[] header) {
        return Utils.byteArrToInt(Arrays.copyOfRange(header, 1, 5));  // to не включительно
    }

    int getCurrBlockSize(byte[] header) {

        return Utils.byteArrToInt(Arrays.copyOfRange(header, 5, 9));
    }


    private boolean isBlockOccupied(byte[] header) {
        return Utils.byteToBool(header[0]);
    }

    private void changeHeaderOccupiedToOpposite(int indexOfByte) {
        if (mem[indexOfByte] == Utils.boolToByte(false)) {
            mem[indexOfByte] = Utils.boolToByte(true);
        } else mem[indexOfByte] = Utils.boolToByte(false);
    }
/*
    private void changeNextBlockSizeHeader(int indexOfStartHeader , int size){
        byte [] newSize = Utils.exchangeIntToByteArray(size);
        System.arraycopy(newSize,0,mem,indexOfStartHeader+5,newSize.length);
    }

    private void changePrevBlockSizeHeader(int indexOfStartHeader , int size , byte [] arr){
        byte [] newSize = Utils.exchangeIntToByteArray(size);
        System.arraycopy(newSize,0,mem,indexOfStartHeader+1,newSize.length);
    }
    */


    public static int offsetSize(int size) {
        if (size % 4 == 0) {
            return size;
        } else return size + (4 - (size % 4));
    }


    public byte[] getMem() {
        return mem;
    }

    public static int getHeader() {
        return HEADER;
    }
}
