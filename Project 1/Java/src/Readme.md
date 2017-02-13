RSA Algorithm for UAkron 435

Command line arguments:

java -cp rsa435.jar Main 
* Creates public and private keys

java -cp rsa435.jar Main <FILE_NAME>
* Uses previously created private keys to encrypt SHA checksum

java -cp rsa435.jar Main <SIGNED_FILE_NAME>
* Uses previously created public key to retrieve SHA checksum
