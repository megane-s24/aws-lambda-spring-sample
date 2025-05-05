# lambda-spring-sample

Spring Boot アプリケーションを AWS Lambda 上で動かすための実装サンプルです。API Gateway（HTTP API）を使って REST リクエストを受け付けます。

## 構成

- Spring Boot
- AWS Lambda
- API Gateway (REST API)
- Lambda Java Runtime
- 単一のエントリポイント (`APIGatewayHandler`)

## Lambda 側の設定

- ハンドラー: `megane.s24.spring_boot.lambda.APIGatewayHandler::handleRequest`
- ランタイム: Java 21
- デプロイパッケージ: ZIP または JAR ファイル

## API Gateway 側の設定

- **タイプ**: REST API
- **ルート**: `ANY /{proxy+}`
  - Lambda プロキシ統合を使用
- **ステージ**: 任意

## 注意

- このプロジェクトは実験・学習用です。
- 詳細なエラーハンドリングやバリデーションは未実装です。
