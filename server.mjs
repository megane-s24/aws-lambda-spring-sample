import http from "http";
import { URL } from "url";
import { spawn } from "child_process";
import readline from "readline";

const port = "3000";
const jarPath = "./target/lambda-spring-0.0.1-SNAPSHOT.jar";

class LambdaRequest {
  constructor(method, path, query, multiValueQuery, body) {
    this.httpMethod = method;
    this.path = path;
    this.queryStringParameters = query;
    this.multiValueQueryStringParameters = multiValueQuery;
    this.body = body;
  }
}

class JarBridge {
  constructor(jarPath) {
    this.jarProcess = spawn(
      "java",
      [
        "-Dfile.encoding=UTF-8",
        "-cp",
        jarPath,
        "megane.s24.lambda.spring.Main",
      ],
      {
        stdio: ["pipe", "pipe", "inherit"],
      }
    );
    this.jarProcess.stdout.setEncoding("utf8");
    this.jarProcess.stdin.setEncoding("utf8");

    this.rl = readline.createInterface({
      input: this.jarProcess.stdout,
      crlfDelay: Infinity,
    });

    this.queue = [];

    this.rl.on("line", (line) => {
      console.log(line);
      if (!line.startsWith("レスポンス:")) return;

      const { resolve } = this.queue.shift() || {};
      if (!resolve) return;

      const jsonPart = line.slice("レスポンス:".length);
      try {
        resolve(JSON.parse(jsonPart));
      } catch (err) {
        console.error(err);
      }
    });
  }

  async sendRequest(jsonString) {
    return new Promise((resolve, reject) => {
      this.queue.push({ resolve, reject });
      this.jarProcess.stdin.write(jsonString + "\n");
    });
  }
}

const jar = new JarBridge(jarPath);

const server = http.createServer(async (req, res) => {
  const chunks = [];
  const url = new URL(req.url, `http://${req.headers.host}`);
  req
    .on("data", (chunk) => chunks.push(chunk))
    .on("end", async () => {
      const body = Buffer.concat(chunks).toString();
      const lambdaReq = new LambdaRequest(
        req.method,
        url.pathname,
        Object.fromEntries(url.searchParams),
        extractMultiValueQueryParams(url),
        body
      );

      const payload = JSON.stringify(lambdaReq);
      console.log(`リクエスト : ${payload}`);

      try {
        const lambdaRes = await jar.sendRequest(payload);

        res.writeHead(lambdaRes.statusCode, lambdaRes.headers || {});
        res.end(lambdaRes.body || "");
      } catch (err) {
        console.error(err);
      }
    });
});

function extractMultiValueQueryParams(url) {
  const multiQuery = {};
  for (const key of url.searchParams.keys()) {
    const values = url.searchParams.getAll(key);
    multiQuery[key] = values;
  }
  return multiQuery;
}

server.listen(port, () => {
  console.log(`サーバー起動:http://localhost:${port}/`);
});
