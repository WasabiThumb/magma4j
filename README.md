# magma4j
A Java 21 implementation of the GOST R 34.12-2015 block cipher 
([RFC 8891](https://datatracker.ietf.org/doc/html/rfc8891)). This is an updated version of the older
GOST 28147-89 ([RFC 5830](https://datatracker.ietf.org/doc/html/rfc5830)) block cipher with support
for 64-bit (**"Magma"**) and 128-bit (**"Kuznyechik"**) blocks. The top-level API defaults to the 64-bit mode,
hence the name.

The [GOST cipher](https://en.wikipedia.org/wiki/GOST_(block_cipher)) was originally developed for the Soviet government
in the 1970s, declassified in 1994, and revised in 2015. Despite its flaws, GOST is still used in some applications
as a [DES](https://en.wikipedia.org/wiki/Data_Encryption_Standard) alternative.

## Usage
### Simple
```java
byte[] key = Magma.generateKey();
// key = Magma.generateKeyFromPassword("password");

byte[] data = "super secret".getBytes(StandardCharsets.UTF_8);

byte[] encrypted = Magma.encrypt(key, data);
byte[] decrypted = Magma.decrypt(key, encrypted);
```

### Custom
```java
MagmaCipher cipher = Magma.newCipher()
    .blockLength(128)
    .mode(CipherMode.CBC)
    .padding(PaddingMethod.PKCS5)
    .build();

// Use cipher#encrypt, cipher#decrypt, etc.
```

### Stream
```java
try (OutputStream os = /* ... */;
     MagmaOutputStream mos = Magma.newOutputStream(os, key)
) {
    // mos.write(...);
}

try (InputStream is = /* ... */;
     MagmaInputStream mis = Magma.newInputStream(is, key)
) {
    // mis.read(...);
}
```

## References
- Thanks to **[gostCrypto](http://web.archive.org/web/20171221084748/http://gostcrypto.com/index.html) by Rudolf 
  Nickolaev** for providing a working implementation of the GOST ciphers to test against

## License
```text
   Copyright 2024 Wasabi Codes

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
```