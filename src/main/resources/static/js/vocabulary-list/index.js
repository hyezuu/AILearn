$(document).ready(function(){
    $('.delete-vocabulary').click(function () {
        Swal.fire({
            title: '정말로 삭제 하시겠습니까?',
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#3085d6',
            cancelButtonColor: '#d33',
            confirmButtonText: '예',
            cancelButtonText: '아니오'
        }).then((result) => {
            if (result.isConfirmed) {
                Swal.fire(
                    '단어가 삭제되었습니다.',
                    '',
                    'success'
                )
            }
        })
    });
});