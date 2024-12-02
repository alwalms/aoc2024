(ns aoc2024.clojure.day2
  (:require [clojure.string :as string]))

(def input (string/split-lines (slurp "resources/2024_2")))

(def parsed-input (->> input
                       (map (fn [line] (string/split line #"\s+")))))

(defn increasing-or-decreasing? [numbers]
  (or (apply < numbers)
      (apply > numbers)))

(defn valid-differences? [numbers]
  (let [diffs (map #(Math/abs (- %1 %2)) numbers (rest numbers))]
    (every? #(and (>= % 1) (<= % 3)) diffs)))

(defn is-valid? [numbers]
  (and (increasing-or-decreasing? numbers)
       (valid-differences? numbers)))

(defn valid-sequence? [line problem-dampener]
  (let [nums (map #(Integer/parseInt %) line)]
    (if (is-valid? nums)
      true
      (if (= true problem-dampener)
        (some is-valid? (map #(concat (take % nums) (drop (inc %) nums))
                             (range (count nums))))
        false))))

(defmulti process (fn [part] part))

(defmethod process :part1 [_]
  (->> parsed-input 
       (map #(valid-sequence? % false))
       (filter true?)
       count))

(defmethod process :part2 [_]
  (->> parsed-input
       (map #(valid-sequence? % true))
       (filter true?)
       count))

(comment
  (process :part1) 
  (process :part2) 
  )
