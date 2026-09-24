import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  stages: [
    { duration: '5s', target: 200 },  // Sobe para 200 VUs em 5s
    { duration: '10s', target: 500 }, // Estabiliza em 500 VUs
    { duration: '5s', target: 0 },    // Baixa
  ],
  thresholds: {
    http_req_failed: ['rate<0.01'],   // Falhas devem ser menor que 1%
    http_req_duration: ['p(95)<500'], // 95% dos requests abaixo de 500ms
  },
};


function gerarCPF() {
  const rand = (n) => Math.floor(Math.random() * n);
  const n = Array.from({ length: 9 }, () => rand(10));

  let d1 = n.reduce((acc, curr, idx) => acc + curr * (10 - idx), 0);
  d1 = 11 - (d1 % 11);
  if (d1 >= 10) d1 = 0;

  let d2 = [...n, d1].reduce((acc, curr, idx) => acc + curr * (11 - idx), 0);
  d2 = 11 - (d2 % 11);
  if (d2 >= 10) d2 = 0;

  return [...n, d1, d2].join('');
}


function obterVotoAleatorio() {
  const opcoes = ['SIM', 'NAO'];
  return opcoes[Math.floor(Math.random() * opcoes.length)];
}

export default function () {
  const url = 'http://localhost:8089/v1/sessoes/3/votos'; // Substitua pelo seu endpoint

  const payload = JSON.stringify({
    associadoCpf: gerarCPF(),
    opcaoVoto: obterVotoAleatorio(),
  });

  const params = {
    headers: {
      'Content-Type': 'application/json',
    },
  };

  const res = http.post(url, payload, params);

  check(res, {
    'status é 200 ou 201': (r) => r.status === 200 || r.status === 201,
  });

  sleep(1);
}