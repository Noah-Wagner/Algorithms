#include <iostream>
#include <vector>
#include <string>
#include <iostream>
#include <sstream>
#include <cassert>
#include "LZW.cpp"

std::string GetFileName(std::string);
std::string SerializeCompressed(std::vector<int> compressed);
std::vector<int> ParseCompressed(std::string);
void WriteDecompressed(std::string, std::string);
std::string ReadCompressed(std::string);
void WriteCompressed(std::string compressed, std::string fileName);

int main(int argc, char *argv[]) {


    if (argc != 3) {
        std::cout << "Usage: lzw c/e <file_name>";
        return -1;
    }

    std::string fileName = argv[2];

    switch (*argv[1]) {
        case 'c': {
            std::ifstream file(fileName);
            std::string file_contents((std::istreambuf_iterator<char>(file)),
                                       std::istreambuf_iterator<char>());
            std::string compressed = Compress(file_contents);
            WriteCompressed(compressed, GetFileName(fileName) + ".lzw");

            std::cout << "Original: " << file_contents << '\n';
            std::string decompressed = Decompress(compressed);
            std::cout << "After:    " << decompressed;
            assert(file_contents == decompressed);

            break;
        }
        case 'e':
            std::string compressedRead = ReadCompressed(fileName);
            std::string decompressed = Decompress(compressedRead);
            std::cout << decompressed;
//            WriteDecompressed(decompressed, GetFileName(fileName) + '2');
            break;
    }

    return 0;
}

std::string GetFileName(std::string file) {
    return file.substr(0, file.find_last_of('.'));
}



void WriteCompressed(std::string compressed, std::string fileName) {
    std::ofstream myfile;
    myfile.open(fileName,  std::ios::binary);

    std::string zeros = "00000000";
    if (compressed.size() % 8 != 0) //make sure the length of the binary string is a multiple of 8
        compressed += zeros.substr(0, 8-compressed.size()%8);
//    std::cout << "Write: " << compressed;
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


void WriteDecompressed(std::string decompressed, std::string fileName) {
    std::ofstream file(fileName);
    if (file.is_open()) {
        file << decompressed;
        file.close();
    }
}

//std::vector<int> ParseCompressed(std::string str) {
//    std::vector<int> compressed;
//    int bits = 9;
//    for (int i = 0; i < str.length(); i += 8) {
//        std::string test = str.substr(i, 8);
//        int thing = BinaryStringToInt(test);
//        compressed.push_back(thing);
//    }
//    return compressed;
//}

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
            uc=uc>>1;
        }
        p = zeros.substr(0, 8-p.size()) + p; //pad 0s to left if needed
        s+= p;
        count++;
    }
    myfile2.close();
    return s;
}