(ns aoc2024.clojure.day5
  (:require [clojure.string :as string]))

(def input (string/split (slurp "resources/2024_5") #"\n\n"))
(def input-top (->> input
                    first
                    string/split-lines
                    (map (fn [line] (string/split line #"\|")))))

(def input-bottom (->> input
                       second
                       string/split-lines
                       (map (fn [line] (string/split line #",")))))

(defn line-errors [line]
  (let [errors (atom 0)]
    (doseq [rule input-top]
      (let [first-pos (.indexOf line (first rule))
            second-pos (.indexOf line (second rule))]
        (when (and (not= first-pos -1) (not= second-pos -1) (> first-pos second-pos))
          (swap! errors inc))))
    @errors))

(defn reorder-line [line]
  (let [rules input-top
        precedence (reduce (fn [m [a b]]
                             (assoc m a (conj (get m a #{}) b)))
                           {}
                           rules)]
    (letfn [(compare-fn [x y]
              (cond
                (contains? (get precedence x #{}) y) -1
                (contains? (get precedence y #{}) x) 1
                :else 0))]
      (vec (sort compare-fn line)))))

(defn get-middle-number [line]
  (Integer/parseInt (get line (int (/ (count line) 2)))))

(defmulti process (fn [part] part))

(defmethod process :part1 [_]
  (->> input-bottom
       (filter (fn [line] (= 0 (line-errors line))))
       (map get-middle-number)
       (reduce +)))

(defmethod process :part2 [_]
  (->> input-bottom
       (filter (fn [line] (< 0 (line-errors line))))
       (map reorder-line)
       (map get-middle-number)
       (reduce +)))

(comment
  (process :part1)
  (process :part2)
  )