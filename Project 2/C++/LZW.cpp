/*
  This code is derived for UA CS435 from LZW@RosettaCode
*/

#include <string>
#include <map>
#include <iostream>
#include <fstream>
#include <iterator>
#include <math.h>
#include <vector> 
#include <sys/stat.h>

int BinaryStringToInt(std::string);
std::string IntToBinaryString(int, int);

const int ASCII_SIZE = 256;
const int BIT_SIZE_LIMIT = 16;

// Compresses a given string to a binary sequence
std::string Compress(const std::string &uncompressed) {

    int dictSize = 256;
    int bitSize = 9;
    std::map<std::string, int> dictionary;
    for (int i = 0; i < ASCII_SIZE; i++) {
        dictionary[std::string(1, i)] = i;
    }

    std::string output = "";
    std::string w;
    double r0 = -1;

    for (int i = 0; i < uncompressed.length(); i++) {
        char c = uncompressed[i];
        std::string wc = w + c;
        if (dictionary.count(wc)) {
            w = wc;
        } else {
            output += IntToBinaryString(dictionary[w], bitSize);
            if (bitSize <= BIT_SIZE_LIMIT) {
                dictionary[wc] = dictSize++;
                if (dictSize >= pow(2, bitSize)) {
                    ++bitSize;
                }
            } else {
                if (r0 == -1) {
                    r0 = (i + 1) * 8.0 / output.length();
                }
                double r1 = (i + 1) * 8.0 / output.length();
                if ((r0 / r1) > 1.05) {
                    r0 = -1;
                    dictionary.clear();
                    dictSize = 256;
                    bitSize = 9;
                    for (int i = 0; i < ASCII_SIZE; i++) {
                        dictionary[std::string(1, i)] = i;
                    }
                }
            }
            w = std::string(1, c);
        }
    }

    if (!w.empty())
        output += IntToBinaryString(dictionary[w], bitSize);
    return output;
}

// Decompresses a given binary sequence to a string
std::string Decompress(std::string compressed) {
    int dictSize = 256;
    std::map<int, std::string> dictionary;
    for (int i = 0; i < 256; i++) {
        dictionary[i] = std::string(1, i);
    }
    int bitSize = 9;
    std::string w = dictionary[BinaryStringToInt(compressed.substr(0, bitSize))];
    std::string result = w;
    std::string entry;

    for (int i = bitSize; i < compressed.length(); i+= bitSize) {
        if (dictSize >= pow(2, bitSize) - 1) {
            ++bitSize;
        }
        int k = BinaryStringToInt(compressed.substr(i, bitSize));
        if (k == 0)
            break;

        if (dictionary.count(k)) {
            entry = dictionary[k];
        } else if (k == dictSize) {
            entry = w + w[0];
        } else {
            throw "Bad compressed k";
        }
        result += entry;
        if (bitSize <= BIT_SIZE_LIMIT) {
            // Add w+entry[0] to the dictionary.
            dictionary[dictSize++] = w + entry[0];
            if (dictSize >= pow(2, bitSize)) {
                ++bitSize;
            }
        }
        w = entry;
    }
    return result;
}

std::string IntToBinaryString(int c, int cl) {
    std::string p = ""; //a binary code string with code length = cl
    while (c > 0) {
        if (c % 2 == 0) {
            p = "0" + p;
        } else {
            p = "1" + p;
        }
        c = c >> 1;
    }
    int zeros = cl - p.size();
    if (zeros < 0) {
        std::cout << "\nWarning: Overflow. code is too big to be coded by " << cl <<" bits!\n";
        p = p.substr(p.size() - cl);
    }
    else {
        for (int i = 0; i < zeros; i++) {  //pad 0s to left of the binary code if needed
            p = "0" + p;
        }
    }
    return p;
}

int BinaryStringToInt(std::string p) {
    int code = 0;
    if (p.size() > 0) {
        if (p.at(0) == '1') {
            code = 1;
        }
        p = p.substr(1);
        while (p.size() > 0) {
            code = code << 1;
            if (p.at(0) == '1') {
                code++;
            }
            p = p.substr(1);
        }
    }
    return code;
}
