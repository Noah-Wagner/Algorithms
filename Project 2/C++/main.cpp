/*
 * Copyright (c) 2017 Noah Wagner.
 *
 * This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

#include <iostream>
#include <vector>
#include <sstream>
#include <cassert>
#include "LZW.cpp"

// Prototypes
std::string GetFileName(std::string);
void WriteDecompressed(std::string, std::string);
std::string ReadCompressed(std::string);
void RunCompression(std::string fileName) ;
void RunExpansion(std::string basic_string);
std::string ReadFile(std::string fileName) ;
void WriteCompressed(std::string compressed, std::string fileName) ;

int main(int argc, char *argv[]) {
    if (argc != 3) {
        std::cout << "Usage: lzw c/e <file_name>";
        return -1;
    }

    std::string fileName = argv[2];

    switch (*argv[1]) {
        case 'c':
            RunCompression(fileName);
            break;
        case 'e':
            RunExpansion(fileName);
            break;
        default:
            return -1;
    }

    return 0;
}

// Reads the uncompressed file, compresses the contents, and writes it to fileName.lzw
void RunCompression(std::string fileName) {
    std::string file_contents = ReadFile(fileName);
    std::string compressed = Compress(file_contents);
    assert(file_contents == Decompress(compressed));
    WriteCompressed(compressed, GetFileName(fileName) + ".lzw");
}

// Reads the compressed file, decompresses the contents, and writes it to fileName2
void RunExpansion(std::string fileName) {
    std::string decompressed = Decompress(ReadCompressed(fileName));
    std::cout << decompressed;
    WriteDecompressed(decompressed, GetFileName(fileName) + '2');
}

// Reads the file given from fileName
std::string ReadFile(std::string fileName) {
    std::ifstream file(fileName);
    return std::string((std::istreambuf_iterator<char>(file)),
                              std::istreambuf_iterator<char>());
}

// Writes the compressed bit sequence to fileName
void WriteCompressed(std::string compressed, std::string fileName) {
    std::ofstream myfile;
    myfile.open(fileName,  std::ios::binary);

    std::string zeros = "00000000";
    if (compressed.size() % 8 != 0) //make sure the length of the binary string is a multiple of 8
        compressed += zeros.substr(0, 8-compressed.size()%8);
    int b;
    for (int i = 0; i < compressed.size(); i += 8) {
        b = 1;
        for (int j = 0; j < 8; j++) {
            b = b << 1;
            if (compressed.at(i+j) == '1') {
                b++;
            }
        }
        char c = (char) (b & 255); //save the string byte by byte
        myfile.write(&c, 1);
    }
    myfile.close();
}

// Reads the compressed bit sequence from fileName
std::string ReadCompressed(std::string fileName) {
    std::string zeros = "00000000";
    std::ifstream myfile2;
    myfile2.open(fileName.c_str(),  std::ios::binary);

    struct stat filestatus;
    stat(fileName.c_str(), &filestatus );
    long fsize = filestatus.st_size; //get the size of the file in bytes

    char c2[fsize];
    myfile2.read(c2, fsize);

    std::string s = "";
    long count = 0;
    while(count < fsize) {
        unsigned char uc =  (unsigned char) c2[count];
        std::string p = ""; //a binary string
        for (int j = 0; j < 8 && uc > 0; j++) {
            if (uc % 2 == 0)
                p="0"+p;
            else
                p="1"+p;
            uc=uc >> 1;
        }
        p = zeros.substr(0, 8-p.size()) + p; //pad 0s to left if needed
        s+= p;
        count++;
    }
    myfile2.close();
    return s;
}

// Writes the decompressed string to fileName
void WriteDecompressed(std::string decompressed, std::string fileName) {
    std::ofstream file(fileName);
    if (file.is_open()) {
        file << decompressed;
        file.close();
    }
}

// Returns fileName without extension
std::string GetFileName(std::string file) {
    return file.substr(0, file.find_last_of('.'));
}
