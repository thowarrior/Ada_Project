// Este código só roda fora do ambiente da caixa.

import fs from 'fs/promises';

const listOfYears = Array.from({length: 300}, (_, index) => 1900 + index);
const allYearsData = {}

const fetchDataYears = async () => {
    try {
        const requests = listOfYears.map(async (year) => {
            const response = await fetch(`https://brasilapi.com.br/api/feriados/v1/${year}`);

            if(!response.ok) throw new Error(`Ocorreu um erro ao buscar ${year}`);

            const dataJson = await response.json();
            allYearsData[year] = dataJson;

            console.table(dataJson);
        });

        await Promise.all(requests);

        const dataJsonStringified = JSON.stringify(allYearsData, null, 2);

        await fs.writeFile('db.json', dataJsonStringified, 'utf-8');

        console.log("Arquivo gravado com sucesso!");
    } catch (error) {
        console.error("Erro ao buscar dados: ", error);
    }
}

await fetchDataYears();
