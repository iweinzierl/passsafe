passsafe
========

PassSafe is an Free Software project. Its purpose is to provide desktop and
mobile apps to manage your personal passwords and keep all instances in sync.


Android
-------
The Android app makes use of the support-library r13 which is currently not
in the central Maven repository. Nevertheless, the Maven module has a
dependency set for this library. To be able to build the project, install
the support-library r13 into your local Maven repository. You can do this with
the following command:

   mvn install:install-file -Dfile=path-to-support-library.jar \
    -DgroupId=com.google.android \
    -DartifactId=support-v13 \
    -Dversion=r13 \
    -Dpackaging=jar
