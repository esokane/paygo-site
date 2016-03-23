**To be done with card processing**
1. Locking a cart while checkout to prevent another thread with same user make one more checkout
2. Check cart in DB and from web site before processing payment
3. Tomcat settings for https connection

**In progress**
1. If report download failed return 404 page
2. If login or signUp on search-results.html page after login user will be redirected to reviewOrder.html page


**Design:**
1. Compile scss(I've changed padding in .input-default in _input.scss because the font
was cut off in Firefox and Chrome.
I've compiled them myself but there're some unused imports and it's better if you take a look on it.  )
2. Alerts with error are ugly