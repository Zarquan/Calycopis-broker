--
-- <meta:header>
--   <meta:licence>
--     Copyright (c) 2024, Manchester (http://www.manchester.ac.uk/)
--
--     This information is free software: you can redistribute it and/or modify
--     it under the terms of the GNU General Public License as published by
--     the Free Software Foundation, either version 3 of the License, or
--     (at your option) any later version.
--
--     This information is distributed in the hope that it will be useful,
--     but WITHOUT ANY WARRANTY; without even the implied warranty of
--     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
--     GNU General Public License for more details.
--
--     You should have received a copy of the GNU General Public License
--     along with this program.  If not, see <http://www.gnu.org/licenses/>.
--   </meta:licence>
-- </meta:header>
--


-- Create our database table.
DROP TABLE IF EXISTS ExecutionBlocks;
CREATE TABLE ExecutionBlocks(
    Ident INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    BlockStart  LONG,
    BlockLength LONG,
    MinCores INT,
    MaxCores INT,
    MinMemory INT,
    MaxMemory INT
    );
SELECT * FROM ExecutionBlocks ;

-- https://stackoverflow.com/a/39394592
DROP VIEW IF EXISTS BlocksView ;
CREATE VIEW BlocksView AS
    (
    SELECT
        *,
        FORMATDATETIME(
            DATEADD('SECOND', (BlockStart * 60 * 5), DATE '1970-01-01'),
            'YYYY-dd-MM HH:mm:ss'
            ) AS StartTime,
        (BlockLength * 5) AS Duration
    FROM
        ExecutionBlocks
    );
SELECT * FROM BlocksView ;

