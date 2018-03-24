(ns clj-text.core
  (:require [clojure.edn :as edn]
            [clojure.walk :as wa]
            [clojure.string :as st]
            [clojure.java.io :as io])
  (:import (java.io StringReader)
           (edu.stanford.nlp.process DocumentPreprocessor
                                     PTBTokenizer
                                     CoreLabelTokenFactory)
           (edu.stanford.nlp.ling CoreLabel
                                  TaggedWord
                                  Word)
           (edu.stanford.nlp.tagger.maxent MaxentTagger)
           (edu.stanford.nlp.trees LabeledScoredTreeNode
                                   LabeledScoredTreeReaderFactory
                                   PennTreeReaderFactory
                                   PennTreebankLanguagePack
                                   TypedDependency
                                   TreeCoreAnnotations$TreeAnnotation)
           (edu.stanford.nlp.semgraph SemanticGraphCoreAnnotations$BasicDependenciesAnnotation)
           (edu.stanford.nlp.util PropertiesUtils)
           (edu.stanford.nlp.parser.common ParserGrammar)
           (edu.stanford.nlp.parser.lexparser LexicalizedParser)
           (edu.stanford.nlp.pipeline Annotation
                                      StanfordCoreNLP)
           (edu.stanford.nlp.ling CoreAnnotations$SentencesAnnotation
                                  CoreAnnotations$TextAnnotation
                                  CoreAnnotations$NamedEntityTagAnnotation
                                  CoreAnnotations$TokensAnnotation
                                  CoreAnnotations$LemmaAnnotation
                                  CoreAnnotations$PartOfSpeechAnnotation
                                  CoreAnnotations$NamedEntityTagAnnotation
                                  Word)
           (edu.stanford.nlp.ie.crf CRFClassifier)
           (java.util Properties)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; work in progress
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn- parse-tree
  [t]
  (let [t (st/replace t #"," "\",\"")]
    (wa/postwalk (fn [x] (if (coll? x)
                           (if (symbol? (second x))
                             (list (keyword (first x)) (name (second x)))
                             (cons (keyword (first x)) (rest x)))
                           x))
                 (edn/read-string t))))

(defn- sentence-annotations
  ([atext] (sentence-annotations atext [:tree :base-depend]))
  ([atext annotations]
   (let [m {:tree [TreeCoreAnnotations$TreeAnnotation (partial parse-tree)]
            :base-depend [SemanticGraphCoreAnnotations$BasicDependenciesAnnotation
                          identity]}
         a (select-keys m annotations)]
     (map (fn [x] (->> (map (fn [[k [t f]]] {k (f (.get x t))}) a) (apply merge)))
          (.get atext CoreAnnotations$SentencesAnnotation)))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; api
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn pipeline
  [& {:keys [options annotators]
      :or {options []
           annotators ["tokenize" "ssplit" "pos" "lemma" "ner"]}}]
  (let [args (-> (concat ["annotators" (->> (map name annotators)
                                            (interpose ",")
                                            (apply str))]
                         options)
                 into-array)]
    (StanfordCoreNLP. (PropertiesUtils/asProperties args))))

(defn annotate
  [text pipeline]
  (.process pipeline text))

(defn token-annotations
  ([atext] (token-annotations atext [:token :lemma :ner :text :pos :regexner :tree]))
  ([atext annotations]
   (let [m {:token CoreAnnotations$TextAnnotation
            :lemma CoreAnnotations$LemmaAnnotation
            :ner CoreAnnotations$NamedEntityTagAnnotation
            :text CoreAnnotations$TextAnnotation
            :pos CoreAnnotations$PartOfSpeechAnnotation
            :regexner CoreAnnotations$NamedEntityTagAnnotation
            :tree TreeCoreAnnotations$TreeAnnotation}
         a (select-keys m annotations)]
     (map (fn [x] (merge (->> (map (fn [[k v]] {k (.get x v)}) a) (apply merge))
                        {:begin (.beginPosition x) :end (.endPosition x)}))
          (.get atext CoreAnnotations$TokensAnnotation)))))
