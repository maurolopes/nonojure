[:body
    {:font-family "Intuitive"
     :overflow-y "scroll"}]

[:.number-text
    {:font-family "Colibri"}]

[:.header
    {:text-transform "uppercase"
     :font-size "2.7em"
     :margin "0px"}]

[:.head-div
    {:position "relative"
     :width "200px"
     :margin-left "auto"
     :margin-right "auto"}]

[:#navigation
    {:margin "20px auto"
     :display "table"}

    [:.tab-button
        {:font-size "25px"
         :color "#888"
         :display "inline-block"
         :margin "10px 20px"
         :border-bottom "3px solid white"}

        [:&.active
            {:color "#000"
             :border-bottom "3px solid black"}

            [:&:hover
                {:color "#000"
                 :border-bottom "3px solid black"
                 :cursor "default"}]]

        [:&:hover
         {:cursor "pointer"
          :border-bottom "3px solid #888"}]]]

[:.hidden {:display "none"}]