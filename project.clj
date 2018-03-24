(defproject clj-text "0.1.0"
  :description "A wrapper around Stanford CoreNLP"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [clj-fuzzy "0.4.1"]
                 [edu.stanford.nlp/stanford-corenlp "3.8.0"]
                 [edu.stanford.nlp/stanford-corenlp "3.8.0" :classifier "models-english"]
                 [edu.stanford.nlp/stanford-corenlp "3.8.0" :classifier "models-spanish"]])
