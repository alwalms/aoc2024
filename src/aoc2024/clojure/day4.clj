(ns aoc2024.clojure.day4
  (:require [clojure.string :as string]))

(def input (string/split-lines (slurp "resources/2024_4")))

(defn coordinates [grid word]
  (for [y (range (count grid))
        x (range (count (nth grid y)))
        :when (= (first word) (nth (nth grid y) x))]
    [y x]))

(defn options [directions x y word]
  (map (fn [[dx dy]]
         (map (fn [i] [(+ x (* i dx)) (+ y (* i dy))]) (range (count word))))
       directions))

(defn xmas-options [x y word]
  (let [directions [[0 1] [1 1] [1 0] [1 -1] [0 -1] [-1 -1] [-1 0] [-1 1]]
        options (options directions x y word)]
    {[x y] options}))

(defn mas-options [x y word]
  (let [directions [[1 1] [1 -1] [-1 -1] [-1 1]]
        options (options directions x y word)]
    {[x y] options}))

(defn detect-word [grid options word]
  (let [get-char (fn [[x y]]
                   (if (and (>= x 0) (< x (count grid))
                            (>= y 0) (< y (count (nth grid x))))
                     (nth (nth grid x) y nil)
                     nil))]
    (->> options
         vals
         (mapcat identity)
         (filter #(= word (apply str (map get-char %)))))))

(defmulti process (fn [part] part))

(defmethod process :part1 [_]
  (let [word "XMAS"]
    (->> (coordinates input word)
         (map (fn [coord] (xmas-options (first coord) (second coord) word)))
         (map (fn [options] (detect-word input options word)))
         (map (fn [x] (count x)))
         (reduce +))))

(defmethod process :part2 [_]
  (let [word "MAS"]
    (->> (coordinates input word)
         (map (fn [coord] (mas-options (first coord) (second coord) word)))
         (map (fn [options] (detect-word input options word)))
         (remove empty?)
         (mapcat (fn [sublist] (map second sublist)))
         frequencies
         vals
         (filter #(> % 1))
         count)))

(comment
  (process :part1)
  (process :part2) 
  )