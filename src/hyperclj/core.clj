(ns hyperclj.core
  "A Clojure library designed to write hyperscript flexibly.

  Use fundamental Clojure data structures to write hyperscript.

  The conversion rules are as follows:

  1. A keyword is converted to a string.
  2. Arguments will be joined by a space.
  3. A vector will have different behavior depending on its metadata. 
     If it has ^:call metadata, it will be treated as a function call.
     Default behavior is to convert it to a string.
  4. A map will be converted to a JSON string.
  5. A string will be converted to a JSON string.
  6. A number will be converted to a JSON string.
  "
  (:require [clojure.test :as t]
            [clojure.string :as string]
            [clojure.data.json :as json]))

(defmulti print*
  "Prints a hyperclj expression as a string."
  #(-> % meta ffirst))

(defn __
  "Converts arguments from Clojure data structures to hyperscript code. See the unit tests for examples."
  [& code]
  (->> code
       (reduce
        (fn [acc x]
          (str
           acc
           (if (empty? acc) "" " ")
           (cond
             (keyword? x) (name x)
             (vector? x) (print* x)
             :else (json/write-str x :escape-unicode false :escape-slash false))))
        "")))

(t/deftest test-hyperclj
  (t/testing "basic"
    (t/are
     [x y] (= x y)
      (__ :on :click :set :my.innerText :to ["#first.innerText + ' ' + #second.innerText"])
      "on click set my.innerText to #first.innerText + ' ' + #second.innerText"
      (__ :on :keyup
          :if :the ["event's"] :key :is "Escape"
          :set :my :value :to "" :trigger :keyup
          :else :show ["<blockquote/>"] :in :#quotes
          :when :its :textContent :contains :my :value)
      "on keyup if the event's key is \"Escape\" set my value to \"\" trigger keyup else show <blockquote/> in #quotes when its textContent contains my value"
      (__ :on :dragstart :call ^:call [:event.dataTransfer.setData "text/plain" :target.textContent])

      "on dragstart call event.dataTransfer.setData(\"text/plain\", target.textContent)"))

  (t/testing "dynamic values"
    (let [foo 123
          bar "abc"]
      (t/is (= (__ :on foo :click ^:id [bar])
               "on 123 click #abc")))))

(comment
  (t/run-tests))

(defmethod print* :default [x]
  (str (first x)))

(defmethod print* :call [x]
  (let [[f & args] x]
    (str (__ f) "(" (string/join ", " (map __ args)) ")")))

(defmethod print* :id [x]
  (str "#" (first x)))
