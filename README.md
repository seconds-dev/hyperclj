# hyperclj

A Clojure library designed for seamless `_hyperscript` authoring. Eliminate the hassle of multi-line strings, string concatenation, and substitution.


# Examples

```clojure
(let [uuid (java.util.UUID/randomUUID)]
    [:input {:type "text"
             :_ (__ :on :keyup :set :my :value :to uuid)}])
```

```clojure
(__ :on :dragstart :call ^:call [:event.dataTransfer.setData "text/plain" :target.textContent])
```
