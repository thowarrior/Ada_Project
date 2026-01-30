import express from 'express';
import fs from 'fs/promises';

const PORT = 3000;
const app = express();

try {
    const dataFile = await fs.readFile("./db.json", "utf-8");
    const dataJSON = JSON.parse(dataFile);

    const PATH_PREFIX = '/api/feriados/v1'
    app.use(express.json());

    app.get(`${PATH_PREFIX}/:year`, (req, res) => {
        const year = req.params['year'];
        const data = dataJSON[year];
        res.json(data);
    })

    app.listen(PORT, () => {
        console.log(`Servidor rodando em: http://localhost:${PORT}`);
    })

    
} catch (error) {
    console.error('Erro ao tentar recuperar dados: ', error);
}

