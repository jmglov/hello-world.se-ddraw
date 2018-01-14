# ddraw

A [re-frame](https://github.com/Day8/re-frame) distributed drawing application.

## AWS Documentation

* [Amazon Cognito Identity SDK for JavaScript](https://github.com/aws/amazon-cognito-identity-js#usage)
* [AWS JavaScript SDK - SNS](https://docs.aws.amazon.com/AWSJavaScriptSDK/latest/AWS/SNS.html)
* [AWS JavaScript SDK - SQS](https://docs.aws.amazon.com/AWSJavaScriptSDK/latest/AWS/SQS.html)

## Development Mode

### Run application:

```
lein clean
lein figwheel dev
```

Figwheel will automatically push cljs changes to the browser.

Wait a bit, then browse to [http://localhost:3449](http://localhost:3449).

### Start Cider from Emacs:

Put this in your Emacs config file:

```
(setq cider-cljs-lein-repl
	"(do (require 'figwheel-sidecar.repl-api)
         (figwheel-sidecar.repl-api/start-figwheel!)
         (figwheel-sidecar.repl-api/cljs-repl))")
```

Navigate to a clojurescript file and start a figwheel REPL with `cider-jack-in-clojurescript` or (`C-c M-J`)

## Production Build


To compile clojurescript to javascript:

```
lein clean
lein cljsbuild once min
```
