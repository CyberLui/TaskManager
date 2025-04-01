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
               
            };
            
            fetch(`/tasks/${userId}`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(newTask)
            })
            .then(response => {
                if (!response.ok) {
                    throw new Error("Erro ao salvar tarefa!");
                }
                return response.json();
            })
            .then(savedTask => {
                renderTask(savedTask);
                document.getElementById("taskTitle").value = "";
                document.getElementById("taskDescription").value = "";
            })
            .catch(error => {
                console.error("Erro:", error);
                alert(error.message || "Erro ao salvar tarefa!");
            });
        });
    }
    
    // Função para exibir nova tarefa no front-end
    function renderTask(task) {
        const taskList = document.getElementById("taskList");
        
        // Formatar a data de criação
        let creationDateHtml = '';
        if (task.creationDate) {
            const date = new Date(task.creationDate);
            const formattedDate = date.toLocaleDateString('pt-BR');
            const formattedTime = date.toLocaleTimeString('pt-BR', {hour: '2-digit', minute:'2-digit'});
            creationDateHtml = `
                <div class="creation-date text-muted mt-2">
                    <small>Criado em ${formattedDate} às ${formattedTime}</small>
                </div>
            `;
        }
        
        const taskHTML = `
            <div class="col-md-4">
                <div class="card task-card mb-4" data-id="${task.id}">
                    <div class="card-body">
                        <h5 class="card-title editable" onclick="editField(this, 'title')">${task.title}</h5>
                        <p><strong>Prioridade:</strong>
                            <span class="task-priority" onclick="editPriority(this)">${task.priority}</span>
                        </p>
                        <p><strong>Status:</strong>
                            <span class="task-status" onclick="editStatus(this)">${task.status}</span>
                        </p>
                        <button class="btn btn-primary toggle-details-btn">Detalhes</button>
                        <div class="task-details mt-3" style="display: none;">
                            <p><strong>Descrição:</strong> 
                                <span class="task-description editable" onclick="editField(this, 'description')">${task.description}</span>
                            </p>
                        </div>
                        <button class="btn btn-danger btn-sm" onclick="confirmDelete(event)">Deletar</button>
                        ${creationDateHtml}
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
    window.confirmDelete = confirmDelete;
    window.deleteTask = deleteTask;
    window.editField = editField;
});

// Função para confirmar e deletar tarefa
function confirmDelete(event) {
    if (confirm("Tem certeza que deseja excluir esta tarefa?")) {
        deleteTask(event);
    }
}

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
    .catch(error => alert(error.message || "Erro ao deletar tarefa!"));
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
        .catch(error => alert(error.message || "Erro ao atualizar prioridade!"));
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
        .catch(error => alert(error.message || "Erro ao atualizar status!"));
    }
}

// Função para editar título e descrição
function editField(element, fieldName) {
    const currentValue = element.textContent.trim();
    const newValue = prompt(`Editar ${fieldName === 'title' ? 'título' : 'descrição'}:`, currentValue);
    
    if (newValue !== null && newValue.trim() !== '' && newValue !== currentValue) {
        const taskId = element.closest('.task-card').dataset.id;
        const updateData = {};
        updateData[fieldName] = newValue.trim();
        
        fetch(`/tasks/${taskId}`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(updateData)
        })
        .then(response => {
            if (!response.ok) throw new Error(`Erro ao atualizar ${fieldName}!`);
            element.textContent = newValue.trim(); // Atualiza no front-end
        })
        .catch(error => alert(error.message || `Erro ao atualizar ${fieldName}!`));
    }
}
