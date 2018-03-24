# clj-text

A simple wrapper around Stanford CoreNLP.

## Install

Add the following to your project.clj:

[org.clojars.s312569/clj-text "0.1.0"]

## Usage

To create a CoreNLP pipeline use the `pipeline` function and the
keyword arguments `:annotators` and `:options`. The default value of
`annotators` is `["tokenize" "ssplit" "pos" "lemma" "ner"]` and an
empty vector for `options`:

```clojure
clj-text.core> (def p (pipeline))
#'clj-text.core/p
clj-text.core> (class p)
edu.stanford.nlp.pipeline.StanfordCoreNLP
clj-text.core> (def cp (pipeline :options ["ner.model" "english.all.3class.caseless.distsim.crf.ser.gz"]))
#'clj-text.core/cp
clj-text.core> 
```

Annotations can then be performed on text using `annotate`:

```clojure
clj-text.core> (def a (annotate "Jason was here with paul" p))
#'clj-text.core/a
clj-text.core> (class a)
edu.stanford.nlp.pipeline.Annotation
clj-text.core>
```

Token annotations can be accessed using `token-annotations`:

```clojure
clj-text.core> (token-annotations a)
({:token "Jason", :lemma "Jason", :ner "PERSON", :text "Jason", :pos "NNP",
 :regexner "PERSON", :tree nil, :begin 0, :end 5} {:token "was", :lemma "be",
 :ner "O", :text "was", :pos "VBD", :regexner "O", :tree nil, :begin 6, :end 9}
 {:token "here", :lemma "here", :ner "O", :text "here", :pos "RB", ...
clj-text.core> (token-annotations a [:ner])
({:ner "PERSON", :begin 0, :end 5} {:ner "O", :begin 6, :end 9}
 {:ner "O", :begin 10, :end 14} {:ner "O", :begin 15, :end 19}
 {:ner "O", :begin 20, :end 24})
clj-text.core> 

## License

Copyright © 2018 Jason Mulvenna

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
