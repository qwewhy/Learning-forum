-- 使用数据库
USE learning_forum;

-- 清空数据（按照外键关系顺序删除）
-- 查看每个表有多少行
SELECT COUNT(*) FROM question_bank_question;
SELECT COUNT(*) FROM question;
SELECT COUNT(*) FROM question_bank;

-- 使用带条件的DELETE语句删除所有数据
DELETE FROM question_bank_question WHERE questionBankId > 0;
DELETE FROM question WHERE id > 0;
DELETE FROM question_bank WHERE id > 0;

-- 插入新数据（按照依赖关系顺序插入）
-- 1. 插入题库表数据
INSERT INTO question_bank (title, description, picture, userId)
VALUES ('Periodic Table', 'Questions on the basic knowledge of the periodic table',
        'https://pic.code-nav.cn/mianshiya/question_bank_picture/1777886594896760834/JldkWf9w_JavaScript.png', 1),
       ('Organic Chemistry', 'Structure, properties and reaction mechanisms of organic compounds',
        'https://pic.code-nav.cn/mianshiya/question_bank_picture/1777886594896760834/QatnFmEN_CSS.png', 2),
       ('Inorganic Chemistry', 'Basic knowledge of inorganic elements and their compounds', 'https://www.mianshiya.com/logo.png', 3),
       ('Analytical Chemistry', 'Questions related to qualitative analysis, quantitative analysis, and instrumental analysis', 'https://www.mianshiya.com/logo.png', 1),
       ('Physical Chemistry', 'Principles of thermodynamics, kinetics, and electrochemistry in physical chemistry', 'https://www.mianshiya.com/logo.png', 2),
       ('Biochemistry', 'Biological macromolecules and biochemical metabolic processes', 'https://www.mianshiya.com/logo.png', 3),
       ('Chemical Thermodynamics', 'Energy changes and equilibrium in chemical reactions', 'https://www.mianshiya.com/logo.png', 1),
       ('Chemical Kinetics', 'Chemical reaction rates and mechanisms', 'https://www.mianshiya.com/logo.png', 2),
       ('Electrochemistry', 'Redox reactions and battery principles', 'https://www.mianshiya.com/logo.png', 3),
       ('Chemical Bond Theory', 'Basic knowledge of various types of chemical bonds', 'https://www.mianshiya.com/logo.png', 1),
       ('Molecular Structure', 'Geometric configuration and spatial structure of molecules', 'https://www.mianshiya.com/logo.png', 2),
       ('Laboratory Safety', 'Laboratory safety and protection techniques', 'https://www.mianshiya.com/logo.png', 3),
       ('Quantum Chemistry', 'Applications of quantum mechanics in chemistry', 'https://www.mianshiya.com/logo.png', 1),
       ('Polymer Chemistry', 'Synthesis and properties of polymer compounds', 'https://www.mianshiya.com/logo.png', 2),
       ('Medicinal Chemistry', 'Design and synthesis of drug molecules', 'https://www.mianshiya.com/logo.png', 3),
       ('Environmental Chemistry', 'Detection and treatment of environmental pollutants', 'https://www.mianshiya.com/logo.png', 1),
       ('Materials Chemistry', 'Preparation and application of chemical materials', 'https://www.mianshiya.com/logo.png', 2),
       ('Nuclear Chemistry', 'Radioactive elements and nuclear reactions', 'https://www.mianshiya.com/logo.png', 3),
       ('Coordination Chemistry', 'Structure and properties of coordination compounds', 'https://www.mianshiya.com/logo.png', 1),
       ('Chemical Engineering Principles', 'Basic principles and processes of chemical industry', 'https://www.mianshiya.com/logo.png', 2);

-- 2. 插入题目表数据
INSERT INTO question (title, content, tags, answer, userId)
VALUES ('Periodic Table Laws', 'Please explain the periodic laws in the periodic table.', '["Periodic Table", "Basics"]',
        'Elements in the periodic table are arranged according to atomic number and have periodically changing properties such as atomic radius, ionization energy, and electronegativity. Within the same period from left to right, metallic properties decrease while non-metallic properties increase.', 1),
       ('Carbon Atom Hybridization', 'Explain the concept of sp³ hybridization and its application in carbon atoms.', '["Organic Chemistry", "Hybridization"]',
        'sp³ hybridization refers to the hybridization of one s orbital and three p orbitals to form four equivalent hybrid orbitals pointing to the four vertices of a regular tetrahedron. The carbon atom in methane molecule is sp³ hybridized.', 2),
       ('Acid-Base Theories', 'Compare the similarities and differences between Brønsted-Lowry and Lewis acid-base theories.', '["Inorganic Chemistry", "Acid-Base Theory"]',
        'Brønsted-Lowry theory defines acids as proton donors and bases as proton acceptors; Lewis theory defines acids as electron pair acceptors and bases as electron pair donors. Lewis theory is more extensive.', 3),
       ('Gas Chromatography Principles', 'What are the basic principles of gas chromatography?', '["Analytical Chemistry", "Chromatography"]',
        'Gas chromatography is based on the different distribution coefficients of different components in a sample between the stationary and mobile phases, causing them to pass through the chromatographic column at different rates, thus achieving component separation.', 1),
       ('Chemical Equilibrium Constant', 'Please explain the relationship between the chemical equilibrium constant K and the free energy change ΔG of a reaction.', '["Physical Chemistry", "Thermodynamics"]',
        'The relationship between the chemical equilibrium constant K and the free energy change ΔG is: ΔG = -RT ln K, where R is the gas constant and T is the absolute temperature. When ΔG < 0, the reaction occurs spontaneously.', 2),
       ('Protein Structure Levels', 'Describe the four levels of protein structure.', '["Biochemistry", "Protein"]',
        'Protein structure is divided into primary structure (amino acid sequence), secondary structure (local structures such as α-helix and β-sheet), tertiary structure (three-dimensional folding of the entire polypeptide chain), and quaternary structure (combination of multiple polypeptide chains).', 3),
       ('Oxidation-Reduction Reactions', 'What are oxidation-reduction reactions? How to balance oxidation-reduction equations?', '["Electrochemistry", "Redox"]',
        'Oxidation-reduction reactions are reactions involving electron transfer, where one substance is oxidized (loses electrons) and another substance is reduced (gains electrons). Equations can be balanced using the half-reaction method or the ion-electron method.', 1),
       ('Nomenclature of Coordination Compounds', 'Explain the systematic nomenclature rules for coordination compounds.', '["Coordination Chemistry", "Nomenclature"]',
        'In naming coordination compounds, write the cation first, followed by the anion; arrange ligands in alphabetical order; use Greek numerical prefixes to indicate the number of ligands; indicate the oxidation state of the metal with Roman numerals.', 2),
       ('Nuclear Reaction Equations', 'How to write and balance nuclear reaction equations?', '["Nuclear Chemistry", "Nuclear Reactions"]',
        'Nuclear reaction equations need to balance mass number (superscript) and atomic number (subscript), the sum of these two numbers must be conserved before and after the reaction. For example: ₁H² + ₁H² → ₂He³ + ₀n¹.', 3),
       ('Types of Polymerization Reactions', 'List and explain several common types of polymerization reactions.', '["Polymer Chemistry", "Polymerization Reactions"]',
        'Common types of polymerization reactions include addition polymerization (such as free radical polymerization, ionic polymerization), condensation reactions (such as esterification, amidation), and ring-opening polymerization, etc.', 1),
       ('Laboratory Safety Rules', 'List several basic safety rules for chemistry laboratories.', '["Laboratory Safety", "Basic Rules"]',
        'Chemistry laboratory safety rules include: wearing protective equipment, knowing emergency procedures, properly handling chemical waste, not eating or drinking in the laboratory, and following protocols when handling hazardous materials, etc.', 2),
       ('Characteristics of Covalent Bonds', 'What is a covalent bond? Describe its basic characteristics.', '["Chemical Bond Theory", "Covalent Bond"]',
        'A covalent bond is a chemical bond formed between atoms by sharing electron pairs. It has characteristics such as directionality, saturation, and length. Its strength is related to the number of shared electron pairs and the degree of overlap between atoms.', 3),
       ('Second Law of Thermodynamics', 'Please explain the Second Law of Thermodynamics and its guiding significance for chemical reactions.', '["Chemical Thermodynamics", "Second Law"]',
        'The Second Law of Thermodynamics states that spontaneous processes are always accompanied by an increase in the entropy of the system, which can be used to predict the direction of chemical reactions. Reactions with ∆G < 0 can occur spontaneously.', 1),
       ('Molecular Orbital Theory', 'What is molecular orbital theory? How is it applied to explain chemical bonds in molecules?', '["Quantum Chemistry", "Molecular Orbitals"]',
        'Molecular orbital theory considers that electrons in molecules belong to the entire molecule rather than individual atoms. Molecular orbitals are formed by linear combinations of atomic orbitals and can explain properties such as bond order and paramagnetism.', 2),
       ('Resonance Structure of Benzene', 'Please explain the resonance theory of benzene and the reason for its stability.', '["Organic Chemistry", "Resonance Theory"]',
        'Benzene has a resonance structure that can be represented by multiple Kekulé structures. In reality, the electrons are delocalized, with π electrons evenly distributed on the ring. This conjugated structure gives benzene special stability.', 3),
       ('Reaction Order and Reaction Mechanism', 'Please explain the relationship between reaction order and reaction mechanism.', '["Chemical Kinetics", "Reaction Mechanism"]',
        'Reaction order is experimentally determined and indicates the effect of concentration on reaction rate; reaction mechanism is theoretically inferred and describes the microscopic process of the reaction. The two do not always correspond and need to be verified through experiments.', 1),
       ('Electrode Potential', 'Please explain standard electrode potential and its applications in electrochemistry.', '["Electrochemistry", "Electrode Potential"]',
        'Standard electrode potential is the potential of a half-cell measured relative to the standard hydrogen electrode under standard conditions. It can be used to calculate cell electromotive force, predict the direction of redox reactions, and calculate reaction free energy.', 2),
       ('Isotope Techniques', 'Compare different types of isotopes and their applications in chemical research.', '["Nuclear Chemistry", "Isotopes"]',
        'Isotopes are divided into stable isotopes and radioactive isotopes. They can be used to trace reaction pathways, determine the age of compounds, study reaction mechanisms, and perform structural analysis, etc.', 3),
       ('Chiral Molecules', 'Please explain the concept of chiral molecules and their importance in medicinal chemistry.', '["Medicinal Chemistry", "Chirality"]',
        'Chiral molecules are molecules that cannot be superimposed on their mirror images, usually containing chiral centers. In medicinal chemistry, different enantiomers may have different biological activities and toxicities, making stereoselective synthesis of chiral drugs crucial.', 1),
       ('Green Chemistry Principles', 'What is green chemistry? List several principles of green chemistry.', '["Environmental Chemistry", "Green Chemistry"]',
        'Green chemistry is a method for designing chemical products and processes to reduce or eliminate the use and generation of hazardous substances. Principles include: waste prevention, atom economy, reducing toxicity, use of renewable raw materials, etc.', 2);

-- 3. 插入题库题目关联表数据
INSERT INTO question_bank_question (questionBankId, questionId, userId)
VALUES (1, 1, 1),
       (1, 2, 1),
       (1, 3, 1),
       (1, 4, 1),
       (1, 5, 1),
       (1, 6, 1),
       (1, 7, 1),
       (1, 8, 1),
       (1, 9, 1),
       (1, 10, 1),
       (2, 2, 2),
       (2, 14, 2),
       (3, 3, 3),
       (3, 13, 3),
       (4, 4, 1),
       (4, 16, 1),
       (5, 5, 2),
       (5, 18, 2),
       (6, 6, 3),
       (6, 19, 3),
       (7, 7, 1),
       (7, 11, 1),
       (8, 8, 2),
       (8, 10, 2),
       (9, 9, 3),
       (9, 17, 3),
       (10, 12, 1),
       (10, 20, 1);

-- 显示结果确认
SELECT 'question_bank 表数据:' AS message;
SELECT * FROM question_bank;

SELECT 'question 表数据:' AS message;
SELECT * FROM question;

SELECT 'question_bank_question 表数据:' AS message;
SELECT * FROM question_bank_question;

-- 确认数据已全部转换为英文
SELECT 'question_bank 题库总数:' AS message, COUNT(*) AS count FROM question_bank;
SELECT 'question 题目总数:' AS message, COUNT(*) AS count FROM question;
SELECT 'question_bank_question 关联总数:' AS message, COUNT(*) AS count FROM question_bank_question;