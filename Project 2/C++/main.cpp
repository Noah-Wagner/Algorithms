#include <iostream>
#include <vector>
#include <iostream>
#include "LZW.cpp"

int main(int argc, char *argv[]) {

    argc = 2;
    *argv[0] = 'c';
    argv[1] = "case1.txt";

    if (argc != 2) {
        std::cout << "Usage: lzw c/e <file_name>";
        return -1;
    }

    std::string fileName = argv[1];

    std::ifstream file(fileName);

    std::string file_contents((std::istreambuf_iterator<char>(file)),
                     std::istreambuf_iterator<char>());

    switch (*argv[0]) {
        case 'c':

            break;
        case 'e':
            break;
        default:
            break;
    }
    std::vector<int> compressed;
    compress("TOBEORNOTTOBEORTOBEORNOT", std::back_inserter(compressed));
    copy(compressed.begin(), compressed.end(), std::ostream_iterator<int>(std::cout, ", "));
    std::cout << std::endl;
    std::string decompressed = decompress(compressed.begin(), compressed.end());
    std::cout << decompressed << std::endl;

    binaryIODemo(compressed);

    return 0;
}