# lein-rb

A Leiningen plugin that facilitates integration with bundler. Heavily
inspired by [lein-ruby](https://github.com/mental/lein-ruby), but
cleaned up and updated for Leiningen 2.

By default, gems are installed to `vendor/gems`, but this can be configured.

*Note*: Assumes Ruby 1.9 compatability.

## Usage

Put `[lein-rb "0.1.0-SNAPSHOT"]` into the `:plugins` vector of your project.clj.

You'll also need to explicitly add a dependency on `org.jruby/jruby-complete`.

Install all gems:

    $ lein bundle

If you'd like gems to automatically fetched when you run `lein deps`,
add `[leiningen.ruby.hooks]` to your project's `:hooks` vector.


## License

Copyright © 2014 Joshua Davey

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
