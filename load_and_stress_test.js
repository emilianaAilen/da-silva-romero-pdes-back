import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  // A number specifying the number of VUs to run concurrently.
  // A string specifying the total duration of the test run.
  
  stages: [
    { duration: '30s', target: 10 },
    { duration: '1m', target: 10000 },
    { duration: '30s', target: 0 },
  ],

  // The following section contains configuration options for execution of this
  // test script in Grafana Cloud.
  //
  // See https://grafana.com/docs/grafana-cloud/k6/get-started/run-cloud-tests-from-the-cli/
  // to learn about authoring and running k6 test scripts in Grafana k6 Cloud.
  //
  // cloud: {
  //   // The ID of the project to which the test is assigned in the k6 Cloud UI.
  //   // By default tests are executed in default project.
  //   projectID: "",
  //   // The name of the test in the k6 Cloud UI.
  //   // Test runs with the same name will be grouped.
  //   name: "script.js"
  // },

  // Uncomment this section to enable the use of Browser API in your tests.
  //
  // See https://grafana.com/docs/k6/latest/using-k6-browser/running-browser-tests/ to learn more
  // about using Browser API in your test scripts.
  //
  // scenarios: {
  //   // The scenario name appears in the result summary, tags, and so on.
  //   // You can give the scenario any name, as long as each name in the script is unique.
  //   ui: {
  //     // Executor is a mandatory parameter for browser-based tests.
  //     // Shared iterations in this case tells k6 to reuse VUs to execute iterations.
  //     //
  //     // See https://grafana.com/docs/k6/latest/using-k6/scenarios/executors/ for other executor types.
  //     executor: 'shared-iterations',
  //     options: {
  //       browser: {
  //         // This is a mandatory parameter that instructs k6 to launch and
  //         // connect to a chromium-based browser, and use it to run UI-based
  //         // tests.
  //         type: 'chromium',
  //       },
  //     },
  //   },
  // }
};

// The function that defines VU logic.
//
// See https://grafana.com/docs/k6/latest/examples/get-started-with-k6/ to learn more
// about authoring k6 scripts.
//

const base_url = "http://localhost:8080/api"
let authToken = '';
const userID = '7b65f5f8-eaca-429f-b527-610a8ddb7d6b'

export function setup() {
  const loginUrl = base_url + '/auth/login'; // Cambia al endpoint de login
  const payload = JSON.stringify({
    email: 'gaston.dasilvagenova@gmail.com', 
    password: 'Loco1234567',
  });

  const headers = { 'Content-Type': 'application/json' };
  const loginRes = http.post(loginUrl, payload, { headers });

  // Validar el login y extraer el token
  check(loginRes, {
    'login exitoso': (r) => r.status === 200,
    'token recibido': (r) => JSON.parse(r.body).token !== undefined,
  });

  const responseBody = JSON.parse(loginRes.body);

  authToken = responseBody.token;
  console.log(authToken);

  return { authToken };

}

export default function(data ) {
  const { authToken } = data;
  const url = base_url + '/products/categories';

  if (!authToken ) {
    throw new Error('authToken no está definido. Revisa el setup.');
  }

  testGetCategoriesEndpoint(authToken);
  testPurchaseEndpoint(authToken);
  testPurchaseAndCommentEndpoint(authToken);

  sleep(1); // Esperar 1 segundo entre solicitudes
}

function testGetCategoriesEndpoint( authToken) {
  const url = base_url + '/products/categories';
  const headers = {
    'Cookie': `authToken=${authToken}`,
  };

  const res = http.get(url, { headers });

  check(res, {
    'GET categories - status es 200': (r) => r && r.status === 200,
    'GET categories - la respuesta no está vacía': (r) => r && r.body && r.body.length > 0,
  });
}

function testPurchaseEndpoint(authToken) {
  const productId = 'MLA1955503348'; // Cambia según sea necesario
  const url =  base_url + `/purchase/${productId}`;
  const headers = {
    'Content-Type': 'application/json',
    'Cookie': `authToken=${authToken}`,
  };

  const payload = JSON.stringify({
    userID: userID,
    cantStockBuyed: 10,
    priceBuyed: 1500,
    puntage: 5,
    productID: productId,
  });

  const res = http.post(url, payload, { headers });

  check(res, {
    'POST purchase - status es 200': (r) => r && r.status === 200,
    'POST purchase - respuesta tiene datos': (r) => r && r.body && r.body.includes('success'),
  });

}

function testPurchaseAndCommentEndpoint(authToken) {
  const urlGetPurchases = base_url + `/purchase/${userID}`;
  const urlPostComment = base_url + '/purchase/comment/';

  const headers = {
    'Content-Type': 'application/json',
    'Cookie': `authToken=${authToken}`,
  };

  // Paso 1: Obtenemos las compras del usuario
  const res = http.get(urlGetPurchases, { headers });

  check(res, {
    'GET purchasesProducts - status es 200': (r) => r && r.status === 200,
    'GET purchasesProducts - respuesta tiene datos': (r) => r && r.body && r.body.length > 0,
  });

  if (res.status === 200 && res.body.length > 0) {

    const purchases = JSON.parse(res.body);
    const firstPurchase = purchases[0]; 
    const purchaseProductId = firstPurchase.id;

    // Paso 2: Hacemos el POST para agregar un comentario a ese producto
    const commentPayload = JSON.stringify({
      purchaseProductId: purchaseProductId,
      description: 'Excelente producto, muy satisfecho.',
      likes: 5,
    });

    const resPostComment = http.post(urlPostComment + purchaseProductId, commentPayload, { headers });

    check(resPostComment, {
      'POST comment - status es 200': (r) => r && r.status === 200,
      'POST comment - respuesta tiene datos': (r) => r && r.body && r.body.includes('comment product created successfully'), // Ajusta según la respuesta esperada
    });
  } else {
    console.log('No se encontraron compras para el usuario.');
  }
}