# dbciupdater 0.0.1-PROTOTYPE
Database updater for legacy products as separated application for ci/cd

usage (11 java version):

Put scripts into your folder (for instance /data/ relative path) and name it in lexicographical order as execution order.

Good format: ddMMyyyy-n-[name].sql 
Example: 07112020-1-createUserTable.sql

Run command line in folder with build jar application:

java -jar app.jar -dbms postgresql -dbname database -port 5432 -scripts /data/ -user root -password password

You can build it as native image with GraalVM (but that way wasn't tested yet and works only for Linux OS)
