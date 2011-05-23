# Cereal

Cereal is a simple serialization library with pluggable backends. It supports serialization and byte appending with the Protobuf and Reader backends, and serialization with basic Java serialization.

Cereal is still very much in the planning and experimentation stage, so things are likely to change on a whim very often. Be careful when relying on snapshots.

# Getting it.

You can get Cereal from Clojars with cake. Just add this to your `:dependencies`:

    [cereal "0.1.1"]

# Usage

Pick a backend and encode some data! It's easy. Here is an REPL session using the Java serialization backend:

    user=> (use '[cereal format java])
    nil
    user=> (def format (make))
    #'user/format
    user=> (encode format (range 10))
    #<byte[] [B@71c1b2>
    user=> (decode format *1)
    (0 1 2 3 4 5 6 7 8 9)

Easy enough, right? Other backends will be just as easy.

# Notes

To use the protobuf format, you need to depend on clojure-protobuf. You'd almost certainly want to do this anyway, because its cake plugin makes it really easy to work with protobufs. You should depend on whatever version of clojure-protobuf that cereal is tested with. You can find out what that version is by looking at its project.clj file.