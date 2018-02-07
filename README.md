# FileSystemSimulation
The project is coded on java. 
It contains 6 classes: 
Sector: abstract class with forward and backward attributes.
Directory: contain directory information
Directory Block (which extends Sector): a directory sector and each can have 31 directory entries.
File Block (which extends Sector): a file sector and each can save at most 504 characters.
Command: realize create, open, delete, close, read, write, seek command 
Main: execute the mapping command with correct operated permission

Operated permission: 
Delete, create, open have the same level permission.
After successfully created data file, only write and close can be executed
After successfully created directory file, can keep on delete, create, open operation
After successfully opened data file, only read, write, seek and then close can be executed. And different open made has different permissions on read, write and seek.

Caution:
Each command should strictly follow the format, otherwise, won’t work well.
Especially:
when seek, the offset must be exactly one number, otherwise, the program will crash.
when read and write, the n also must be one number, otherwise, the program will crash.
