document.addEventListener("DOMContentLoaded", function () {
    const taskForm = document.getElementById("taskForm");
    
    // Função para adicionar nova tarefa
    if (taskForm) {
        taskForm.addEventListener("submit", function (event) {
            event.preventDefault();
            const title = document.getElementById("taskTitle").value;
            const description = document.getElementById("taskDescription").value;
            const userId = taskForm.getAttribute("data-user-id");
            
            if (title.trim() === "" || description.trim() === "") {
                alert("Por favor, preencha todos os campos!");
                return;
            }
            
            const newTask = {
                title: title.trim(),
                description: description.trim(),
                priority: "MÉDIO", // Valor padrão
                status: "PENDENTE" // Valor padrão
            };
            
            fetch(`/tasks/${userId}`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(newTask)
            })
            .then(response => response.ok ? response.json() : Promise.reject("Erro ao salvar tarefa!"))
            .then(savedTask => {
                renderTask(savedTask);
                document.getElementById("taskTitle").value = "";
                document.getElementById("taskDescription").value = "";
            })
            .catch(error => alert(error));
        });
    }
    
    // Função para exibir nova tarefa no front-end
    function renderTask(task) {
        const taskList = document.getElementById("taskList");
        const taskHTML = `
            <div class="col-md-4">
                <div class="card task-card mb-4" data-id="${task.id}">
                    <div class="card-body">
                        <h5 class="card-title">${task.title}</h5>
                        <p><strong>Prioridade:</strong>
                            <span class="task-priority" onclick="editPriority(this)">${task.priority}</span>
                        </p>
                        <p><strong>Status:</strong>
                            <span class="task-status" onclick="editStatus(this)">${task.status}</span>
                        </p>
                        <button class="btn btn-primary toggle-details-btn">Detalhes</button>
                        <div class="task-details mt-3" style="display: none;">
                            <p><strong>Descrição:</strong> ${task.description}</p>
                        </div>
                        <button class="btn btn-danger btn-sm" onclick="deleteTask(event)">Deletar</button>
                    </div>
                </div>
            </div>
        `;
        taskList.insertAdjacentHTML("beforeend", taskHTML);
        attachToggleEvents(); // Atualiza os eventos de detalhes
    }
    
    // Função para alternar exibição de detalhes
    function attachToggleEvents() {
        const toggleButtons = document.querySelectorAll('.toggle-details-btn');
        toggleButtons.forEach(button => {
            button.removeEventListener("click", toggleDetails); // Evita duplicação de eventos
            button.addEventListener("click", toggleDetails);
        });
    }
    
    function toggleDetails(event) {
        const details = event.target.closest('.task-card').querySelector('.task-details');
        details.style.display = details.style.display === "none" ? "block" : "none";
    }
    
    // Inicializa eventos de exibição de detalhes
    attachToggleEvents();
    
    // Expor as funções ao escopo global
    window.editPriority = editPriority;
    window.editStatus = editStatus;
    window.deleteTask = deleteTask;
});

// Função para deletar tarefa
function deleteTask(event) {
    const button = event.target;
    const taskId = button.closest('.task-card').dataset.id;
    if (!taskId) {
        alert("Erro: ID da tarefa não encontrado!");
        return;
    }
    
    fetch(`/tasks/${taskId}`, { method: "DELETE" })
    .then(response => {
        if (response.ok) {
            button.closest('.col-md-4').remove();
        } else {
            throw new Error("Erro ao deletar tarefa!");
        }
    })
    .catch(error => alert(error));
}

// Função para editar prioridade
function editPriority(element) {
    const priorities = ["LOW", "MEDIUM", "HIGH"];
    const newPriority = prompt("Nova prioridade (LOW, MEDIUM, HIGH):", element.textContent);
    
    if (newPriority && priorities.includes(newPriority)) {
        const taskId = element.closest('.task-card').dataset.id;
        
        fetch(`/tasks/${taskId}`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ priority: newPriority })
        })
        .then(response => {
            if (!response.ok) throw new Error("Erro ao atualizar prioridade!");
            element.textContent = newPriority; // Atualiza no front-end
        })
        .catch(error => alert(error));
    }
}

// Função para editar status
function editStatus(element) {
    const statuses = ["PENDENTE", "EM_ANDAMENTO", "FINALIZADA"];
    const newStatus = prompt("Novo status (PENDENTE, EM_ANDAMENTO, FINALIZADA):", element.textContent);
    
    if (newStatus && statuses.includes(newStatus)) {
        const taskId = element.closest('.task-card').dataset.id;
        
        fetch(`/tasks/${taskId}`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ status: newStatus })
        })
        .then(response => {
            if (!response.ok) throw new Error("Erro ao atualizar status!");
            element.textContent = newStatus; // Atualiza no front-end
        })
        .catch(error => alert(error));
    }
}
