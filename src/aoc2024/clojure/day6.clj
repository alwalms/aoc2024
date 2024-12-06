(ns aoc2024.clojure.day6
  (:require [clojure.string :as string]))

(def input (slurp "resources/2024_6"))

(defn parse-input [input]
  (let [lines (string/split-lines input)
        grid (into {} (for [r (range (count lines))
                            c (range (count (nth lines r)))]
                        [[r c] (nth (nth lines r) c)]))]
    {:grid grid
     :start (first (for [[pos char] grid
                         :when (#{\^ \> \v \<} char)]
                     pos))
     :direction (case (grid (first (for [[pos char] grid
                                         :when (#{\^ \> \v \<} char)]
                                     pos)))
                  \^ :up
                  \> :right
                  \v :down
                  \< :left)}))

(defn part1 [input]
  (let [{:keys [grid start direction]} (parse-input input)
        directions {:up [-1 0] :right [0 1] :down [1 0] :left [0 -1]}
        turn-right {:up :right :right :down :down :left :left :up}]
    (loop [position start
           dir direction
           visited #{start}]
      (let [[dr dc] (directions dir)
            next-pos [(+ (first position) dr) (+ (second position) dc)]]
        (cond
          (nil? (grid next-pos)) (count visited)
          (= \# (grid next-pos))
          (recur position (turn-right dir) visited)
          :else
          (recur next-pos dir (conj visited next-pos)))))))

(defn simulate-move [grid start direction]
  (let [directions {:up [-1 0] :right [0 1] :down [1 0] :left [0 -1]}
        turn-right {:up :right :right :down :down :left :left :up}]
    (loop [position start
           dir direction
           visited #{[start direction]}]
      (let [[dr dc] (directions dir)
            next-pos [(+ (first position) dr) (+ (second position) dc)]]
        (cond
          (nil? (grid next-pos)) false
          (visited [next-pos dir]) true
          (= \# (grid next-pos))
          (recur position (turn-right dir) (conj visited [position (turn-right dir)]))
          :else
          (recur next-pos dir (conj visited [next-pos dir])))))))

(defn find-loop-causing-positions [grid start direction]
  (filter
   (fn [pos]
     (when (= \. (grid pos))
       (let [modified-grid (assoc grid pos \#)]
         (simulate-move modified-grid start direction))))
   (keys grid)))

(defmulti process (fn [part] part))
(defmethod process :part1 [_] (part1 input))
(defmethod process :part2 [_]
  (let [{:keys [grid start direction]} (parse-input input)]
    (count (find-loop-causing-positions grid start direction))))

(comment
  (process :part1)
  (process :part2)
  )