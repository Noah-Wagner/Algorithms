#include <iostream>
#include <vector>
#include <string>
#include <iostream>
#include <sstream>
#include "LZW.cpp"

std::string GetFileName(std::string);
std::vector<int> ReadCompressed();
void WriteDecompressed(std::string, std::string);
std::vector<int> ParseCompressed(std::string);
void WriteCompressed(std::vector<int> vector, std::string fileName);

int main(int argc, char *argv[]) {
    if (argc != 2) {
        std::cout << "Usage: lzw c/e <file_name>";
        return -1;
    }

    std::string fileName = argv[1];

    std::ifstream file(fileName);

    std::string file_contents((std::istreambuf_iterator<char>(file)),
                               std::istreambuf_iterator<char>());

    std::vector<int> compressed;

    switch (*argv[0]) {
        case 'c':
            compressed = Compress(file_contents);
            WriteCompressed(compressed, GetFileName(fileName) + ".lzw");
            break;
        case 'e':
            compressed = ParseCompressed(file_contents);
            std::string decompressed = Decompress(compressed);
            WriteDecompressed(decompressed, GetFileName(fileName) + '2');
            break;
        default:break;
    }

    return 0;
}

std::string GetFileName(std::string file) {
    return file.substr(0, file.find_last_of('.'));
}

void WriteCompressed(std::vector<int> vector, std::string fileName) {
    std::stringstream write_string;
    copy(vector.begin(), vector.end(), std::ostream_iterator<int>(write_string, ","));
    std::ofstream file(fileName);
    if (file.is_open()) {
        file << write_string.rdbuf();
        file.close();
    }

}

void WriteDecompressed(std::string decompressed, std::string fileName) {
    std::ofstream file(fileName);
    if (file.is_open()) {
        file << decompressed;
        file.close();
    }
}

std::vector<int> ParseCompressed(std::string file_content) {
    std::stringstream ss;
    ss.str(file_content);
    std::string line;
    std::vector<int> compressed;
    while (std::getline(ss, line, ',')) {
        compressed.push_back(std::stoi(line));
    }
    return compressed;
}